package uk.ac.ebi.fg.annotare2.web.server.services.utils.tsv;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public interface TsvLineVisitor {

    boolean accepts(List<String> values);
}
