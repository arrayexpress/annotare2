/**
 * This class is generated by jOOQ
 */
package uk.ac.ebi.fg.annotare2.ae.jooq.tables;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.1.0" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ScOwner extends org.jooq.impl.TableImpl<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.ScOwnerRecord> {

	private static final long serialVersionUID = -1699585944;

	/**
	 * The singleton instance of <code>AE2.SC_OWNER</code>
	 */
	public static final uk.ac.ebi.fg.annotare2.ae.jooq.tables.ScOwner SC_OWNER = new uk.ac.ebi.fg.annotare2.ae.jooq.tables.ScOwner();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.ScOwnerRecord> getRecordType() {
		return uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.ScOwnerRecord.class;
	}

	/**
	 * The column <code>AE2.SC_OWNER.ID</code>. 
	 */
	public final org.jooq.TableField<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.ScOwnerRecord, java.math.BigInteger> ID = createField("ID", org.jooq.impl.SQLDataType.DECIMAL_INTEGER.precision(19), this);

	/**
	 * The column <code>AE2.SC_OWNER.SC_LABEL_ID</code>. 
	 */
	public final org.jooq.TableField<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.ScOwnerRecord, java.math.BigInteger> SC_LABEL_ID = createField("SC_LABEL_ID", org.jooq.impl.SQLDataType.DECIMAL_INTEGER.precision(19), this);

	/**
	 * The column <code>AE2.SC_OWNER.SC_USER_ID</code>. 
	 */
	public final org.jooq.TableField<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.ScOwnerRecord, java.math.BigInteger> SC_USER_ID = createField("SC_USER_ID", org.jooq.impl.SQLDataType.DECIMAL_INTEGER.precision(19), this);

	/**
	 * Create a <code>AE2.SC_OWNER</code> table reference
	 */
	public ScOwner() {
		super("SC_OWNER", uk.ac.ebi.fg.annotare2.ae.jooq.Ae2.AE2);
	}

	/**
	 * Create an aliased <code>AE2.SC_OWNER</code> table reference
	 */
	public ScOwner(java.lang.String alias) {
		super(alias, uk.ac.ebi.fg.annotare2.ae.jooq.Ae2.AE2, uk.ac.ebi.fg.annotare2.ae.jooq.tables.ScOwner.SC_OWNER);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.ScOwnerRecord> getPrimaryKey() {
		return uk.ac.ebi.fg.annotare2.ae.jooq.Keys.PK_SC_OWNER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.ScOwnerRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.ScOwnerRecord>>asList(uk.ac.ebi.fg.annotare2.ae.jooq.Keys.PK_SC_OWNER_ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.ForeignKey<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.ScOwnerRecord, ?>> getReferences() {
		return java.util.Arrays.<org.jooq.ForeignKey<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.ScOwnerRecord, ?>>asList(uk.ac.ebi.fg.annotare2.ae.jooq.Keys.FK_SC_OWNER_SLID_SC_LABEL_ID, uk.ac.ebi.fg.annotare2.ae.jooq.Keys.FK_SC_OWNER_SUID_SC_USER_ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public uk.ac.ebi.fg.annotare2.ae.jooq.tables.ScOwner as(java.lang.String alias) {
		return new uk.ac.ebi.fg.annotare2.ae.jooq.tables.ScOwner(alias);
	}
}
