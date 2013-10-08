/**
 * This class is generated by jOOQ
 */
package uk.ac.ebi.fg.annotare2.autosubs.jooq;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.1.0" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AeAutosubs extends org.jooq.impl.SchemaImpl {

	private static final long serialVersionUID = 997870593;

	/**
	 * The singleton instance of <code>ae_autosubs</code>
	 */
	public static final AeAutosubs AE_AUTOSUBS = new AeAutosubs();

	/**
	 * No further instances allowed
	 */
	private AeAutosubs() {
		super("ae_autosubs");
	}

	@Override
	public final java.util.List<org.jooq.Table<?>> getTables() {
		java.util.List result = new java.util.ArrayList();
		result.addAll(getTables0());
		return result;
	}

	private final java.util.List<org.jooq.Table<?>> getTables0() {
		return java.util.Arrays.<org.jooq.Table<?>>asList(
			uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.DataFiles.DATA_FILES,
			uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.Experiments.EXPERIMENTS,
			uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.Roles.ROLES,
			uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.RolesUsers.ROLES_USERS,
			uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.Spreadsheets.SPREADSHEETS,
			uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.Users.USERS);
	}
}
