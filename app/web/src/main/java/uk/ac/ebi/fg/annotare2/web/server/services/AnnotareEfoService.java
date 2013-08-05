package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.magetabcheck.efo.EfoService;
import uk.ac.ebi.fg.annotare2.magetabcheck.efo.EfoTerm;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
public class AnnotareEfoService implements EfoService {

    private static final Logger log = LoggerFactory.getLogger(AnnotareEfoService.class);

    private static final int MAX_HITS = 1000;

    private final EfoSearch efoSearch;

    @Inject
    public AnnotareEfoService(EfoSearch efoSearch) {
        this.efoSearch = efoSearch;
        testSearch();
    }

    private void testSearch() {
        List<String> errors = newArrayList();

        String label = "Cell line";
        String accession = "EFO_0000322";
        String rootAccession = "MaterialEntity";
        EfoTerm node = findTermByLabel(label, rootAccession);
        if (node == null) {
            errors.add("Can't find term by label (in branch): '" + label + "' | " + rootAccession);
        }

        node = findTermByAccession(accession, rootAccession);
        if (node == null) {
            errors.add("Can't find term by accession (in branch): " + accession + " | " + rootAccession);
        }

        node = findTermByAccession(accession);
        if (node == null) {
            errors.add("Can't find term by accession: " + accession);
        }

        node = findTermByLabelOrAccession("", accession, rootAccession);
        if (node == null) {
            errors.add("Can't find term by label or accession (in branch): '' | " + accession + " | " + rootAccession);
        }

        node = findTermByLabelOrAccession(label, "", rootAccession);
        if (node == null) {
            errors.add("Can't find term by label or accession (in branch): '" + label + "' | '' | " + rootAccession);
        }

        node = findTermByLabelOrAccession(label, accession, rootAccession);
        if (node == null) {
            errors.add("Can't find term by label or accession (in branch): '" + label + "' | " + accession + " | " + rootAccession);
        }

        String prefix = "cell li";
        Collection<EfoTerm> result = efoSearch.searchByPrefix(prefix, MAX_HITS);
        if (result.isEmpty()) {
            errors.add("Can't find term by prefix: '" + prefix + "'");
        }

        result = efoSearch.searchByPrefix(prefix, rootAccession, MAX_HITS);
        if (result.isEmpty()) {
            errors.add("Can't find term by prefix (in branch): '" + prefix + "' | " + rootAccession);
        }

        if (errors.isEmpty()) {
            log.info("SEARCH TEST: OK");
        } else {
            log.info("SEARCH TEST: FAILED \n" + on("\n").join(errors));
        }
    }

    public Collection<EfoTerm> suggest(String prefix, int limit) {
        return efoSearch.searchByPrefix(prefix, limit);
    }

    public Collection<EfoTerm> suggest(String prefix, String branchAccession, int limit) {
        return efoSearch.searchByPrefix(prefix, branchAccession, limit);
    }

    @Override
    public EfoTerm findTermByLabel(String label, String rootAccession) {
        return efoSearch.searchByLabel(label, rootAccession);
    }

    @Override
    public EfoTerm findTermByAccession(String accession) {
        return efoSearch.searchByAccession(accession);
    }

    @Override
    public EfoTerm findTermByAccession(String accession, String rootAccession) {
        return efoSearch.searchByAccession(accession, rootAccession);
    }

    @Override
    public EfoTerm findTermByLabelOrAccession(String name, String accession, String rootAccession) {
        if (isNullOrEmpty(accession)) {
            if (!isNullOrEmpty(name)) {
                EfoTerm term = findTermByLabel(name, rootAccession);
                if (term != null) {
                    return term;
                }
            }
        } else if (isNullOrEmpty(name)) {
            if (!isNullOrEmpty(accession)) {
                EfoTerm term = findTermByAccession(accession, rootAccession);
                if (term != null) {
                    return term;
                }
            }
        } else {
            EfoTerm term = findTermByAccession(accession, rootAccession);
            if (term != null && name.equalsIgnoreCase(term.getLabel())) {
                return term;
            }
        }
        return null;
    }
}
