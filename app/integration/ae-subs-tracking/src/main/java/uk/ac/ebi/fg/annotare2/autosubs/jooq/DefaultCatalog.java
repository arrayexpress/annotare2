/*
 * This file is generated by jOOQ.
 */
package uk.ac.ebi.fg.annotare2.autosubs.jooq;


import java.util.Arrays;
import java.util.List;

import org.jooq.Schema;
import org.jooq.impl.CatalogImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DefaultCatalog extends CatalogImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>DEFAULT_CATALOG</code>
     */
    public static final DefaultCatalog DEFAULT_CATALOG = new DefaultCatalog();

    /**
     * The schema <code>ae_autosubs</code>.
     */
    public final AeAutosubs AE_AUTOSUBS = AeAutosubs.AE_AUTOSUBS;

    /**
     * No further instances allowed
     */
    private DefaultCatalog() {
        super("");
    }

    @Override
    public final List<Schema> getSchemas() {
        return Arrays.<Schema>asList(
            AeAutosubs.AE_AUTOSUBS);
    }
}
