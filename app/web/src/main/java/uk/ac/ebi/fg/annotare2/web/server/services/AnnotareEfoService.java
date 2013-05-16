package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.inject.Inject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.magetabcheck.MageTabCheckProperties;
import uk.ac.ebi.fg.annotare2.services.efo.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.io.Closeables.close;
import static java.util.Collections.emptyList;
import static org.apache.lucene.document.Field.Store.NO;
import static org.apache.lucene.document.Field.Store.YES;
import static uk.ac.ebi.fg.annotare2.web.server.services.AnnotareEfoService.EfoField.*;

/**
 * @author Olga Melnichuk
 */
public class AnnotareEfoService implements EfoService {

    private static final Logger log = LoggerFactory.getLogger(AnnotareEfoService.class);

    //TODO move to properties
    private static final String indexPath = "/Users/olkin/tmp/efo-index/";

    private static final int MAX_HITS = 1000;

    static enum EfoField {
        ACCESSION_FIELD("accession") {
            @Override
            public Field create(String name, String value) {
                return new StringField(name, value, YES);
            }
        },
        LABEL_FIELD("label") {
            @Override
            public Field create(String name, String value) {
                return new StringField(name, value, YES);
            }
        },
        TEXT_FIELD("text") {
            @Override
            public Field create(String name, String value) {
                return new TextField(name, value, NO);
            }
        },
        ASCENDANT_FIELD("ascendant") {
            @Override
            public Field create(String name, String value) {
                return new StringField(name, value, NO);
            }
        };

        private final String name;

        private EfoField(String name) {
            this.name = name;
        }

        protected abstract Field create(String name, String value);

        public Field create(String value) {
            return create(name, value);
        }

        public String prefixMatches(String prefix) {
            return name + ":\"" + prefix + "*\"";
        }

        public String phraseMatches(String phrase) {
            return name + ":\"" + phrase + "\"";
        }
    }

    @Inject
    public AnnotareEfoService(MageTabCheckProperties properties) {
        load(properties);
        testSearch();
    }

    private void testSearch() {
        List<String> errors = newArrayList();

        String label = "cell line";
        String accession = "EFO_0000322";
        String rootAccession = "MaterialType";
        EfoNode node = findTermByName(label, rootAccession);
        if (node == null) {
            errors.add("Can't find term by label (in branch): " + label + " | " + rootAccession);
        }

        node = findTermByAccession(accession, rootAccession);
        if (node == null) {
            errors.add("Can't find term by accession (in branch): " + accession + " | " + rootAccession);
        }

        node = findTermByAccession(accession);
        if (node == null) {
            errors.add("Can't find term by accession: " + accession);
        }

        node = findTermByNameOrAccession("", accession, rootAccession);
        if (node == null) {
            errors.add("Can't find term by label or accession (in branch): '' | " + accession + " | " + rootAccession);
        }

        node = findTermByNameOrAccession(label, accession, rootAccession);
        if (node == null) {
            errors.add("Can't find term by label or accession (in branch): " + label + " | '' | " + rootAccession);
        }

        String prefix = "cell li";
        Collection<EfoNode> result = suggest(prefix);
        if (result.isEmpty()) {
            errors.add("Can't find term by prefix: '" + prefix + "'");
        }

        result = suggest(prefix, rootAccession);
        if (result.isEmpty()) {
            errors.add("Can't find term by prefix (in branch): '" + prefix + "' | " + rootAccession);
        }

        if (errors.isEmpty()) {
            log.debug("SEARCH TEST: OK");
        } else {
            log.debug("SEARCH TEST: FAILED \n" + on("\n").join(errors));
        }
    }

    @Override
    public EfoNode findTermByName(String name, String rootAccession) {
        try {
            return exactSearchByLabel(name, rootAccession);
        } catch (ParseException e) {
            log.debug("efo search doesn't work", e);
        } catch (IOException e) {
            log.debug("efo search doesn't work", e);
        }
        return null;
    }

    @Override
    public EfoNode findTermByAccession(String accession) {
        try {
            return exactSearchByAccession(accession);
        } catch (ParseException e) {
            log.debug("efo search doesn't work", e);
        } catch (IOException e) {
            log.debug("efo search doesn't work", e);
        }
        return null;
    }

    @Override
    public EfoNode findTermByAccession(String accession, String rootAccession) {
        try {
            return exactSearchByAccession(accession, rootAccession);
        } catch (ParseException e) {
            log.debug("efo search doesn't work", e);
        } catch (IOException e) {
            log.debug("efo search doesn't work", e);
        }
        return null;
    }

    @Override
    public EfoNode findTermByNameOrAccession(String name, String accession, String rootAccession) {
        if (isNullOrEmpty(accession)) {
            if (!isNullOrEmpty(name)) {
                EfoNode term = findTermByName(name, rootAccession);
                if (term != null) {
                    return term;
                }
            }
        } else if (isNullOrEmpty(name)) {
            if (!isNullOrEmpty(accession)) {
                EfoNode term = findTermByAccession(accession, rootAccession);
                if (term != null) {
                    return term;
                }
            }
        } else {
            EfoNode term = findTermByAccession(accession, rootAccession);
            if (term != null && name.equals(term.getName())) {
                return term;
            }
        }
        return null;
    }

    public Collection<EfoNode> suggest(String prefix) {
        try {
            return prefixSearch(prefix);
        } catch (ParseException e) {
            log.debug("efo search doesn't work", e);
        } catch (IOException e) {
            log.debug("efo search doesn't work", e);
        }
        return emptyList();
    }

    public Collection<EfoNode> suggest(String prefix, String rootAccession) {
        try {
            return prefixSearch(prefix, rootAccession);
        } catch (ParseException e) {
            log.debug("efo search doesn't work", e);
        } catch (IOException e) {
            log.debug("efo search doesn't work", e);
        }
        return emptyList();
    }

    private EfoNode exactSearchByLabel(String label, String rootAccession) throws ParseException, IOException {
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);
        QueryParser parser = new QueryParser(Version.LUCENE_43, TEXT_FIELD.name, analyzer);
        Query query = parser.parse(
                LABEL_FIELD.phraseMatches(label.toLowerCase())
                 + " AND " + ASCENDANT_FIELD.phraseMatches(rootAccession.toLowerCase())
        );
        List<EfoNode> result = runQuery(query, MAX_HITS);
        return result.isEmpty() ? null : result.get(0);
    }

    private EfoNode exactSearchByAccession(String accession, String rootAccession) throws ParseException, IOException {
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);
        QueryParser parser = new QueryParser(Version.LUCENE_43, TEXT_FIELD.name, analyzer);
        Query query = parser.parse(
                ACCESSION_FIELD.phraseMatches(accession.toLowerCase()) +
                        " AND " + ASCENDANT_FIELD.phraseMatches(rootAccession.toLowerCase()));
        List<EfoNode> result = runQuery(query, MAX_HITS);
        return result.isEmpty() ? null : result.get(0);
    }

    private EfoNode exactSearchByAccession(String accession) throws ParseException, IOException {
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);
        QueryParser parser = new QueryParser(Version.LUCENE_43, TEXT_FIELD.name, analyzer);
        Query query = parser.parse(
                ACCESSION_FIELD.phraseMatches(accession.toLowerCase()));
        List<EfoNode> result = runQuery(query, MAX_HITS);
        return result.isEmpty() ? null : result.get(0);
    }

    private Collection<EfoNode> prefixSearch(String prefix) throws ParseException, IOException {
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);
        QueryParser parser = new QueryParser(Version.LUCENE_43, TEXT_FIELD.name, analyzer);
        Query query = parser.parse(
                TEXT_FIELD.prefixMatches(prefix.toLowerCase()));
        return runQuery(query, MAX_HITS);
    }

    private Collection<EfoNode> prefixSearch(String prefix, String rootAccession) throws ParseException, IOException {
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);
        QueryParser parser = new QueryParser(Version.LUCENE_43, TEXT_FIELD.name, analyzer);
        Query query = parser.parse(
                TEXT_FIELD.prefixMatches(prefix.toLowerCase()) + " AND " +
                        ASCENDANT_FIELD.phraseMatches(rootAccession.toLowerCase()));
        return runQuery(query, MAX_HITS);
    }

    private List<EfoNode> runQuery(Query query, int maxHits) throws IOException, ParseException {
        IndexReader reader = null;
        try {
            reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
            IndexSearcher searcher = new IndexSearcher(reader);
            log.debug("Searching for: " + query.toString());

            long start = System.currentTimeMillis();
            TopDocs results = searcher.search(query, null, maxHits);
            ScoreDoc[] hits = results.scoreDocs;

            log.debug("[ " + hits.length + " ] hits");

            List<EfoNode> terms = newArrayList();
            for (ScoreDoc hit : hits) {
                Document doc = searcher.doc(hit.doc);
                log.debug("found: " + doc.get("label") + ", " + doc.get("accession"));
                terms.add(new EfoNodeImpl(
                        doc.get(ACCESSION_FIELD.name),
                        doc.get(LABEL_FIELD.name)));
            }
            log.debug("Time: " + (System.currentTimeMillis() - start) + "ms");
            return terms;
        } finally {
            close(reader, true);
        }
    }

    private void load(EfoServiceProperties properties) {
        try {
            EfoLoader efoLoader = new EfoLoader(properties);
            createIndex(efoLoader.load());
        } catch (IOException e) {
            log.error("Can't load EFO", e);

        } catch (OWLOntologyCreationException e) {
            log.error("Can't load EFO", e);
        }
    }

    private void createIndex(EfoGraph graph) throws IOException {
        IndexWriter writer = null;
        try {
            long start = System.currentTimeMillis();
            log.debug("Indexing to directory '" + indexPath + "'...");

            Directory dir = FSDirectory.open(new File(indexPath));
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_43, analyzer);

            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            writer = new IndexWriter(dir, iwc);
            indexDocs(writer, graph);

            log.debug(System.currentTimeMillis() - start + " milliseconds in total");

        } finally {
            close(writer, true);
        }
    }

    private void indexDocs(IndexWriter writer, EfoGraph dag) throws IOException {
        Collection<EfoNode> roots = getRootNodes(dag);
        Set<String> visited = newHashSet();
        for (EfoNode node : roots) {
            indexDoc(node, new ArrayList<EfoNode>(), visited, writer);
        }
    }

    private void indexDoc(EfoNode node, List<EfoNode> parents, Set<String> visited, IndexWriter writer) throws IOException {
        if (visited.contains(node.getAccession())) {
            return;
        }

        visited.add(node.getAccession());

        Document doc = new Document();
        doc.add(ACCESSION_FIELD.create(node.getAccession()));
        doc.add(LABEL_FIELD.create(node.getName()));
        doc.add(TEXT_FIELD.create(node.getName()));

        for (EfoNode parent : parents) {
            doc.add(ASCENDANT_FIELD.create(parent.getAccession()));
        }

        writer.addDocument(doc);

        List<EfoNode> newParents = newArrayList(parents);
        newParents.add(node);
        for (EfoNode child : node.getChildren()) {
            indexDoc(child, newParents, visited, writer);
        }
    }

    private Collection<EfoNode> getRootNodes(EfoGraph graph) {
        try {
            java.lang.reflect.Field f = graph.getClass().getDeclaredField("efoMap");
            f.setAccessible(true);
            Map<String, EfoNode> map = (Map<String, EfoNode>) f.get(graph);
            List<EfoNode> roots = newArrayList();
            for (EfoNode node : map.values()) {
                if (node.getParents().isEmpty()) {
                    roots.add(node);
                }
            }
            return roots;
        } catch (NoSuchFieldException e) {
            log.error("can't hack EfoGraph", e);
        } catch (IllegalAccessException e) {
            log.error("can't hack EfoGraph", e);
        }
        return emptyList();
    }

    private static class EfoNodeImpl implements EfoNode {
        private final String accession;
        private final String name;

        private EfoNodeImpl(String accession, String name) {
            this.accession = accession;
            this.name = name;
        }

        @Override
        public String getAccession() {
            return accession;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Collection<String> getAlternativeNames() {
            //TODO may be add these field to the index?
            return emptyList();
        }

        @Override
        public Collection<? extends EfoNode> getParents() {
            throw new UnsupportedOperationException("Illegal use of node");
        }

        @Override
        public Collection<? extends EfoNode> getChildren() {
            throw new UnsupportedOperationException("Illegal use of node");
        }
    }
}
