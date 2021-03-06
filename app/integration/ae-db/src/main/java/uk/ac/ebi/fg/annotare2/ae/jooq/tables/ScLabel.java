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
public class ScLabel extends org.jooq.impl.TableImpl<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.ScLabelRecord> {

	private static final long serialVersionUID = -1558210683;

	/**
	 * The singleton instance of <code>AE2.SC_LABEL</code>
	 */
	public static final uk.ac.ebi.fg.annotare2.ae.jooq.tables.ScLabel SC_LABEL = new uk.ac.ebi.fg.annotare2.ae.jooq.tables.ScLabel();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.ScLabelRecord> getRecordType() {
		return uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.ScLabelRecord.class;
	}

	/**
	 * The column <code>AE2.SC_LABEL.ID</code>. 
	 */
	public final org.jooq.TableField<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.ScLabelRecord, java.math.BigInteger> ID = createField("ID", org.jooq.impl.SQLDataType.DECIMAL_INTEGER.precision(19), this);

	/**
	 * The column <code>AE2.SC_LABEL.NAME</code>. 
	 */
	public final org.jooq.TableField<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.ScLabelRecord, java.lang.String> NAME = createField("NAME", org.jooq.impl.SQLDataType.VARCHAR.length(400), this);

	/**
	 * Create a <code>AE2.SC_LABEL</code> table reference
	 */
	public ScLabel() {
		super("SC_LABEL", uk.ac.ebi.fg.annotare2.ae.jooq.Ae2.AE2);
	}

	/**
	 * Create an aliased <code>AE2.SC_LABEL</code> table reference
	 */
	public ScLabel(java.lang.String alias) {
		super(alias, uk.ac.ebi.fg.annotare2.ae.jooq.Ae2.AE2, uk.ac.ebi.fg.annotare2.ae.jooq.tables.ScLabel.SC_LABEL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.ScLabelRecord> getPrimaryKey() {
		return uk.ac.ebi.fg.annotare2.ae.jooq.Keys.PK_SC_LABEL_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.ScLabelRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.ScLabelRecord>>asList(uk.ac.ebi.fg.annotare2.ae.jooq.Keys.PK_SC_LABEL_NAME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public uk.ac.ebi.fg.annotare2.ae.jooq.tables.ScLabel as(java.lang.String alias) {
		return new uk.ac.ebi.fg.annotare2.ae.jooq.tables.ScLabel(alias);
	}
}
