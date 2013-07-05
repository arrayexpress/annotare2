/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;
import com.google.inject.Inject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.analyzing.AnalyzingQueryParser;
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
import uk.ac.ebi.fg.annotare2.services.efo.*;
import uk.ac.ebi.fg.annotare2.web.server.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.web.server.services.utils.EfoSubTree;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.io.Closeables.close;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.apache.lucene.document.Field.Store.NO;
import static org.apache.lucene.document.Field.Store.YES;
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
        try {
            return prefixSearch(prefix, limit);
        } catch (ParseException e) {
            logError(e);
        } catch (IOException e) {
            logError(e);
        }
        return emptyList();
    }

    @Override
    public Collection<EfoTerm> searchByPrefix(String prefix, String branchAccession, int limit) {
        try {
            return prefixSearch(prefix, branchAccession, limit);
        } catch (ParseException e) {
            logError(e);
        } catch (IOException e) {
            logError(e);
        }
        return emptyList();
    }

    @Override
    public EfoTerm searchByLabel(String label, String branchAccession) {
        try {
            QueryParser parser = new QueryParser(Version.LUCENE_43, null, new KeywordAnalyzer());
            Query query = parser.parse(
                    LABEL_FIELD_LOWERCASE.matchesPhrase(label.toLowerCase())
                            + " AND " + ASCENDANT_FIELD.matchesPhrase(branchAccession.toLowerCase())
            );
            List<EfoTerm> result = runQuery(query, 1);
            return result.isEmpty() ? null : result.get(0);
        } catch (ParseException e) {
            logError(e);
        } catch (IOException e) {
            logError(e);
        }
        return null;
    }

    @Override
    public EfoTerm searchByAccession(String accession, String branchAccession) {
        try {
            QueryParser parser = new QueryParser(Version.LUCENE_43, null, new KeywordAnalyzer());
            Query query = parser.parse(
                    ACCESSION_FIELD_LOWERCASE.matchesPhrase(accession.toLowerCase())
                            + " AND " + ASCENDANT_FIELD.matchesPhrase(branchAccession.toLowerCase()));
            List<EfoTerm> result = runQuery(query, 1);
            return result.isEmpty() ? null : result.get(0);
        } catch (ParseException e) {
            logError(e);
        } catch (IOException e) {
            logError(e);
        }
        return null;
    }

    @Override
    public EfoTerm searchByAccession(String accession) {
        try {
            QueryParser parser = new QueryParser(Version.LUCENE_43, null, new KeywordAnalyzer());
            Query query = parser.parse(
                    ACCESSION_FIELD_LOWERCASE.matchesPhrase(accession.toLowerCase()));
            List<EfoTerm> result = runQuery(query, 1);
            return result.isEmpty() ? null : result.get(0);
        } catch (ParseException e) {
            logError(e);
        } catch (IOException e) {
            logError(e);
        }
        return null;
    }

    @Override
    public EfoSubTree subtree(String branchAccession) {
        try {
            QueryParser parser = new QueryParser(Version.LUCENE_43, null, new KeywordAnalyzer());
            Query query = parser.parse(ASCENDANT_FIELD.matchesPhrase(branchAccession.toLowerCase()));
            List<Document> docs = runDocumentQuery(query, 1000);

            List<EfoTerm> terms = newArrayList();
            SetMultimap<String, String> parents = HashMultimap.create();
            for (Document doc : docs) {
                String[] values = doc.getValues(ASCENDANT_FIELD.name);
                EfoTerm term = asTerm(doc);
                terms.add(term);
                parents.putAll(term.getAccession(), asList(values));
            }

            for (String id : parents.keySet()) {
                Set<String> parentList = parents.get(id);
                for (String p : parentList) {
                    if (!parents.containsKey(p)) {
                        parents.remove(id, p);
                    }
                }
            }
            return new EfoSubTree(terms).build(parents);
        } catch (ParseException e) {
            logError(e);
        } catch (IOException e) {
            logError(e);
        }
        return null;
    }

    private void logError(Exception e) {
        log.error("EFO search unexpected error", e);
    }

    private Collection<EfoTerm> prefixSearch(String prefix, int limit) throws ParseException, IOException {
        QueryParser parser = new AnalyzingQueryParser(Version.LUCENE_43, TEXT_FIELD.name, new StandardAnalyzer(Version.LUCENE_43));
        Query query = parser.parse(TEXT_FIELD.matchesPrefix(prefix));
        return runQuery(query, limit);
    }

    private Collection<EfoTerm> prefixSearch(String prefix, String rootAccession, int limit) throws ParseException, IOException {
        QueryParser parser = new AnalyzingQueryParser(Version.LUCENE_43, TEXT_FIELD.name, new StandardAnalyzer(Version.LUCENE_43));
        Query query = parser.parse(
                TEXT_FIELD.matchesPrefix(prefix)
                        + " AND " + ASCENDANT_FIELD.matchesPhrase(rootAccession));
        return runQuery(query, limit);
    }

    private List<EfoTerm> runQuery(Query query, int maxHits) throws IOException, ParseException {
        return asTerms(runDocumentQuery(query, maxHits));
    }

    private List<Document> runDocumentQuery(Query query, int maxHits) throws IOException, ParseException {
        IndexReader reader = null;
        try {
            reader = DirectoryReader.open(FSDirectory.open(new File(properties.getEfoIndexDir())));
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
                Collections.<String>emptyList());
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

    private void createIndex(EfoDag graph) throws IOException {
        IndexWriter writer = null;
        try {
            long start = System.currentTimeMillis();
            log.info("Indexing to directory '" + properties.getEfoIndexDir() + "'...");

            Directory dir = FSDirectory.open(new File(properties.getEfoIndexDir()));
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);
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

        Document doc = new Document();
        doc.add(ACCESSION_FIELD.create(node));
        doc.add(ACCESSION_FIELD_LOWERCASE.create(node));
        doc.add(LABEL_FIELD.create(node));
        doc.add(LABEL_FIELD_LOWERCASE.create(node));
        doc.add(TEXT_FIELD.create(node));

        for (EfoNode parent : parents) {
            doc.add(ASCENDANT_FIELD.create(parent));
        }

        writer.addDocument(doc);

        List<EfoNode> newParents = newArrayList(parents);
        newParents.add(node);
        for (EfoNode child : node.getChildren()) {
            indexDoc(child, newParents, visited, writer);
        }
    }

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
        } catch (NoSuchFieldException e) {
            log.error("can't hack EfoGraph", e);
        } catch (IllegalAccessException e) {
            log.error("can't hack EfoGraph", e);
        }
        return emptyList();
    }

    static enum EfoField {
        ACCESSION_FIELD("accession") {
            @Override
            public Field create(String name, EfoNode node) {
                return new StringField(name, node.getAccession(), YES);
            }
        },
        ACCESSION_FIELD_LOWERCASE("accession_lowercase") {
            @Override
            public Field create(String name, EfoNode node) {
                return new StringField(name, node.getAccession().toLowerCase(), NO);
            }
        },
        LABEL_FIELD("label") {
            @Override
            public Field create(String name, EfoNode node) {
                return new StringField(name, node.getName(), YES);
            }
        },
        LABEL_FIELD_LOWERCASE("label_lowercase") {
            @Override
            public Field create(String name, EfoNode node) {
                return new StringField(name, node.getName().toLowerCase(), NO);
            }
        },
        TEXT_FIELD("text") {
            @Override
            public Field create(String name, EfoNode node) {
                return new TextField(name, node.getName().toLowerCase(), NO);
            }
        },
        ASCENDANT_FIELD("ascendant") {
            @Override
            public Field create(String name, EfoNode node) {
                return new StringField(name, node.getAccession().toLowerCase(), NO);
            }
        };

        private final String name;

        private EfoField(String name) {
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
                    return name + ":" + input;
                }
            })) + "*";
        }

        public String matchesPhrase(String phrase) {
            return name + ":\"" + phrase + "\"";
        }
    }
}
