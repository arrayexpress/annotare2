package uk.ac.ebi.fg.annotare2.magetab.table;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public interface TsvLineVisitor {

    boolean accepts(List<String> values);
}
