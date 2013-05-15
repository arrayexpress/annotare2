package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.inject.Inject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.Closeables.close;
import static org.apache.lucene.document.Field.Store.NO;
import static org.apache.lucene.document.Field.Store.YES;

/**
 * @author Olga Melnichuk
 */
public class AnnotareEfoService implements EfoService {

    private static final Logger log = LoggerFactory.getLogger(AnnotareEfoService.class);

    @Inject
    public AnnotareEfoService(MageTabCheckProperties properties) {
        load(properties);
    }

    @Override
    public EfoNode findTermByName(String name, String rootAccession) {
        return null;
    }

    @Override
    public EfoNode findTermByAccession(String accession) {
        return null;
    }

    @Override
    public EfoNode findTermByAccession(String accession, String rootAccession) {
        return null;
    }

    @Override
    public EfoNode findTermByNameOrAccession(String name, String accession, String rootAccession) {
        return null;
    }


    public void load(EfoServiceProperties properties) {
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
        // TODO
        String indexPath = "/Users/olkin/tmp/efo-index/";
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

            log.debug(System.currentTimeMillis() - start + " total milliseconds");

        } finally {
            close(writer, true);
        }
    }

    private void indexDocs(IndexWriter writer, EfoGraph dag) throws IOException {
        Collection<EfoNode> roots = getRootNodes(dag);
        for (EfoNode node : getAllNodes(dag)) {
            indexDoc(node, writer);
        }
    }

    private void indexDoc(EfoNode node, IndexWriter writer) throws IOException {
        Document doc = new Document();
        doc.add(new StringField("accession", node.getAccession(), YES));
        doc.add(new StringField("name", node.getName(), YES));
        //TODO add depth prefixes
        writer.addDocument(doc);
    }

    private Collection<EfoNode> getAllNodes(EfoGraph graph) {
        try {
            java.lang.reflect.Field f = graph.getClass().getDeclaredField("efoMap");
            f.setAccessible(true);
            Map<String, EfoNode> map = (Map<String, EfoNode>) f.get(graph);
            return map.values();
        } catch (NoSuchFieldException e) {
            log.error("can't hack EfoGraph", e);
        } catch (IllegalAccessException e) {
            log.error("can't hack EfoGraph", e);
        }
        return Collections.emptyList();
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
            for (EfoNode node : roots) {
                log.debug(node.getAccession() + " | " + node.getName());
            }
            return roots;
        } catch (NoSuchFieldException e) {
            log.error("can't hack EfoGraph", e);
        } catch (IllegalAccessException e) {
            log.error("can't hack EfoGraph", e);
        }
        return Collections.emptyList();
    }
}
