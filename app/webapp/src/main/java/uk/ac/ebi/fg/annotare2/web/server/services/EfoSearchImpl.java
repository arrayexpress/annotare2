/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
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
import uk.ac.ebi.fg.annotare2.core.components.EfoSearch;
import uk.ac.ebi.fg.annotare2.core.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.magetabcheck.efo.*;
import uk.ac.ebi.fg.annotare2.web.server.services.utils.LuceneEfoTermAnalyzer;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.io.Closeables.close;
import static java.lang.Boolean.parseBoolean;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static uk.ac.ebi.fg.annotare2.web.server.services.EfoSearchImpl.EfoField.*;

/**
 * @author Olga Melnichuk
 */
public class EfoSearchImpl implements EfoSearch {

    private static final Logger log = LoggerFactory.getLogger(EfoSearchImpl.class);

    private final AnnotareProperties properties;

    @Inject
    public EfoSearchImpl(AnnotareProperties properties) {
        this.properties = properties;
        load(properties.getEfoServiceProperties());
    }

    @Override
    public Collection<EfoTerm> searchByPrefix(String prefix, int limit) {
        return prefixSearch(prefix, limit);
    }

    @Override
    public Collection<EfoTerm> searchByPrefix(String prefix, String branchAccession, int limit) {
        return prefixSearch(prefix, branchAccession, limit);
    }

    @Override
    public Collection<EfoTerm> getChildTerms(EfoTerm term, int limit) {
        QueryParser parser = new QueryParser(Version.LUCENE_43, null, new LuceneEfoTermAnalyzer());
        Query query = parse(
                parser
                , PARENT_FIELD.matchesPhrase(term.getAccession().toLowerCase())
        );
        return runQuery(query, limit);
    }

    @Override
    public Collection<EfoTerm> getDescendantTerms(EfoTerm term, int limit) {
        QueryParser parser = new QueryParser(Version.LUCENE_43, null, new LuceneEfoTermAnalyzer());
        Query query = parse(
                parser
                , ASCENDANT_FIELD.matchesPhrase(term.getAccession().toLowerCase())
        );
        return runQuery(query, limit);
    }

    @Override
    public EfoTerm searchByLabel(String label) {
        QueryParser parser = new QueryParser(Version.LUCENE_43, null, new LuceneEfoTermAnalyzer());
        Query query = parse(
                parser
                , LABEL_FIELD_ANALYZED.matchesPhrase(label.toLowerCase())
        );
        List<EfoTerm> result = runQuery(query, 1);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public EfoTerm searchByLabel(String label, String branchAccession) {
        QueryParser parser = new QueryParser(Version.LUCENE_43, null, new LuceneEfoTermAnalyzer());
        Query query = parse(
                parser
                , LABEL_FIELD_ANALYZED.matchesPhrase(label.toLowerCase())
                        + " AND ("
                        + ASCENDANT_FIELD.matchesPhrase(branchAccession.toLowerCase())
                        + " OR "
                        + ACCESSION_FIELD_ANALYZED.matchesPhrase(branchAccession.toLowerCase())
                        + ")"
        );
        List<EfoTerm> result = runQuery(query, 1);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public EfoTerm searchByAccession(String accession, String branchAccession) {
        if (accession.equals(branchAccession)) {
            return searchByAccession(accession);
        }
        QueryParser parser = new QueryParser(Version.LUCENE_43, null, new LuceneEfoTermAnalyzer());
        Query query = parse(
                parser
                , ACCESSION_FIELD_ANALYZED.matchesPhrase(accession.toLowerCase())
                        + " AND " + ASCENDANT_FIELD.matchesPhrase(branchAccession)
        );
        List<EfoTerm> result = runQuery(query, 1);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public EfoTerm searchByAccession(String accession) {
        QueryParser parser = new QueryParser(Version.LUCENE_43, null, new LuceneEfoTermAnalyzer());
        Query query = parse(
                parser
                , ACCESSION_FIELD_ANALYZED.matchesPhrase(accession.toLowerCase())
        );
        List<EfoTerm> result = runQuery(query, 1);
        return result.isEmpty() ? null : result.get(0);
    }

    /*
    @Override
    public EfoTerm getSystemTerm(SystemEfoTerm term) {
        return searchByAccession(properties.getEfoTermAccession(term));
    }
    */

    private Collection<EfoTerm> prefixSearch(String prefix, int limit) {
        QueryParser parser = new QueryParser(
                Version.LUCENE_43,
                TEXT_ANALYZED_FIELD.name,
                new LuceneEfoTermAnalyzer()
        );
        parser.setAllowLeadingWildcard(true);
        Query query = parse(
                parser
                , TEXT_ANALYZED_FIELD.matchesPrefix(prefix)
        );
        return runQuery(query, limit);
    }

    private Collection<EfoTerm> prefixSearch(String prefix, String rootAccession, int limit) {
        QueryParser parser = new QueryParser(
                Version.LUCENE_43,
                TEXT_ANALYZED_FIELD.name,
                new LuceneEfoTermAnalyzer()
        );
        parser.setAllowLeadingWildcard(true);
        Query query = parse(
                parser
                , TEXT_ANALYZED_FIELD.matchesPrefix(prefix)
                        + " AND " + ASCENDANT_FIELD.matchesPhrase(rootAccession)
        );
        return runQuery(query, limit);
    }

    private Query parse(QueryParser parser, String queryString) {
        try {
            return parser.parse(queryString);
        } catch (ParseException e) {
            log.error("Unable to parse [{}]", queryString, e);
        }
        return null;
    }

    private List<EfoTerm> runQuery(Query query, int maxHits) {
        if (null != query) {
            try {
                return asTerms(runDocumentQuery(query, maxHits));
            } catch (IOException e) {
                log.error("Unexpected exception:", e);
            }
        }
        return emptyList();
    }

    private List<Document> runDocumentQuery(Query query, int maxHits) throws IOException {
        IndexReader reader = null;
        try {
            reader = DirectoryReader.open(FSDirectory.open(properties.getEfoIndexDir()));
            IndexSearcher searcher = new IndexSearcher(reader);
            log.debug("Searching for: " + query.toString());

            long start = System.currentTimeMillis();
            TopDocs results = searcher.search(query, null, maxHits);
            ScoreDoc[] hits = results.scoreDocs;

            log.debug("[" + hits.length + "] hits");

            List<Document> docs = newArrayList();
            for (ScoreDoc hit : hits) {
                Document doc = searcher.doc(hit.doc);
                //log.debug("found: " + doc.get(LABEL_FIELD.name) + ", " + doc.get(ASCENDANT_FIELD.name));
                docs.add(doc);
            }
            log.debug("Time: " + (System.currentTimeMillis() - start) + "ms");
            return docs;
        } finally {
            close(reader, true);
        }
    }

    private List<EfoTerm> asTerms(List<Document> docs) {
        return transform(docs, new Function<Document, EfoTerm>() {
            @Nullable
            @Override
            public EfoTerm apply(@Nullable Document input) {
                return asTerm(input);
            }
        });
    }

    private EfoTerm asTerm(Document doc) {
        return new EfoTerm(
                doc.get(ACCESSION_FIELD.name),
                doc.get(LABEL_FIELD.name),
                doc.get(DEFINITION_FIELD.name),
                parseBoolean(doc.get(ORGANISATIONAL_FLAG_FIELD.name)),
                Collections.<String>emptyList(),
                Arrays.asList(doc.getValues(PARENT_FIELD.name)),
                Arrays.asList(doc.getValues(ASCENDANT_FIELD.name)));
    }

    private void load(EfoServiceProperties properties) {
        try {
            EfoLoader efoLoader = new EfoLoader(properties);
            createIndex(efoLoader.load());
        } catch (IOException | OWLOntologyCreationException e) {
            log.error("Unable to load EFO", e);

        }
    }

    private void createIndex(EfoDag graph) throws IOException {
        IndexWriter writer = null;
        try {
            long start = System.currentTimeMillis();
            log.info("Indexing to directory '{}'...", properties.getEfoIndexDir());

            Directory dir = FSDirectory.open(properties.getEfoIndexDir());
            Analyzer analyzer = new LuceneEfoTermAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_43, analyzer);

            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            writer = new IndexWriter(dir, iwc);
            indexDocs(writer, graph);

            log.info("Indexing done in " + (System.currentTimeMillis() - start) + " milliseconds");

        } finally {
            close(writer, true);
        }
    }

    private void indexDocs(IndexWriter writer, EfoDag dag) throws IOException {
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

        Map<String, EfoNode> ascendants = newHashMap();
        collectAscendants(ascendants, node);

        Document doc = new Document();
        doc.add(ACCESSION_FIELD.create(node));
        doc.add(ACCESSION_FIELD_ANALYZED.create(node));
        doc.add(LABEL_FIELD.create(node));
        doc.add(LABEL_FIELD_ANALYZED.create(node));
        doc.add(TEXT_ANALYZED_FIELD.create(node));
        doc.add(DEFINITION_FIELD.create(node));
        doc.add(ORGANISATIONAL_FLAG_FIELD.create(node));

        for (EfoNode parent : node.getParents()) {
            doc.add(PARENT_FIELD.create(parent));
        }

        for (EfoNode ascendant : ascendants.values()) {
            doc.add(ASCENDANT_FIELD.create(ascendant));
        }

        writer.addDocument(doc);

        List<EfoNode> newParents = newArrayList(parents);
        newParents.add(node);
        for (EfoNode child : node.getChildren()) {
            indexDoc(child, newParents, visited, writer);
        }
    }

    private void collectAscendants(Map<String, EfoNode> parents, EfoNode node) {
        for(EfoNode parent : node.getParents()) {
            if (!parents.containsKey(parent.getAccession())) {
                parents.put(parent.getAccession(), parent);
                collectAscendants(parents, parent);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<EfoNode> getRootNodes(EfoDag graph) {
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
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("can't hack efo graph", e);
        }
        return emptyList();
    }

    enum EfoField {
        ACCESSION_FIELD("accession") {
            @Override
            public Field create(String name, EfoNode node) {
                return newField(name, node.getAccession(), false, true);
            }
        },
        ACCESSION_FIELD_ANALYZED("accession_analyzed") {
            @Override
            public Field create(String name, EfoNode node) {
                return newField(name, node.getAccession(), true, false);
            }
        },
        LABEL_FIELD("label") {
            @Override
            public Field create(String name, EfoNode node) {
                return newField(name, nullToEmpty(node.getLabel()), false, true);
            }
        },
        LABEL_FIELD_ANALYZED("label_analyzed") {
            @Override
            public Field create(String name, EfoNode node) {
                return newField(name, nullToEmpty(node.getLabel()), true, false);
            }
        },
        DEFINITION_FIELD("definition") {
            @Override
            protected Field create(String name, EfoNode node) {
                return newField(name, nullToEmpty(node.getDefinition()), false, true);
            }
        },
        ORGANISATIONAL_FLAG_FIELD("organisational_flag") {
            @Override
            protected Field create(String name, EfoNode node) {
                return newField(name, Boolean.toString(node.isOrganisational()), false, true);
            }
        },
        TEXT_ANALYZED_FIELD("text_analyzed") {
            @Override
            public Field create(String name, EfoNode node) {
                return newField(name, nullToEmpty(node.getLabel()).toLowerCase(), true, false);
            }
        },
        ASCENDANT_FIELD("ascendant") {
            @Override
            public Field create(String name, EfoNode node) {
                return newField(name, node.getAccession().toLowerCase(), false, true);
            }
        },
        PARENT_FIELD("parent") {
            @Override
            protected Field create(String name, EfoNode node) {
                return newField(name, node.getAccession().toLowerCase(), false, true);
            }
        };

        private final String name;

        EfoField(String name) {
            this.name = name;
        }

        protected abstract Field create(String name, EfoNode node);

        public Field create(EfoNode node) {
            return create(name, node);
        }

        public String matchesPrefix(String prefix) {
            String[] words = prefix.split("\\s");
            return on(" AND ").join(Lists.transform(asList(words), new Function<String, String>() {
                @Nullable
                @Override
                public String apply(@Nullable String input) {
                    return name + ":" + escapeString(input);
                }
            })) + "*";
        }

        public String matchesPhrase(String phrase) {
            return name + ":\"" + escapeString(phrase) + "\"";
        }

        private static String escapeString(String string) {
            if (null == string) {
                return null;
            }
            return string.replaceAll("(/|\\+|\\-|&&|\\|\\||!|\\(|\\)|\\{|\\}|\\[|\\]|\\^|\"|~|\\*|\\?|:|\\\\)", "\\\\$1");
        }

        private static Field newField(String name, String content, boolean shouldAnalyze, boolean shouldStore) {
            FieldType fieldType = new FieldType();
            fieldType.setIndexOptions(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
            fieldType.setIndexed(true);
            fieldType.setTokenized(shouldAnalyze);
            fieldType.setStored(shouldStore);
            fieldType.setStoreTermVectors(true);
            fieldType.setStoreTermVectorPositions(true);

            return new Field(name, content, fieldType);
        }
    }
}
