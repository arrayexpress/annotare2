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
public class Study extends org.jooq.impl.TableImpl<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.StudyRecord> {

	private static final long serialVersionUID = -320474146;

	/**
	 * The singleton instance of <code>AE2.STUDY</code>
	 */
	public static final uk.ac.ebi.fg.annotare2.ae.jooq.tables.Study STUDY = new uk.ac.ebi.fg.annotare2.ae.jooq.tables.Study();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.StudyRecord> getRecordType() {
		return uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.StudyRecord.class;
	}

	/**
	 * The column <code>AE2.STUDY.ID</code>. 
	 */
	public final org.jooq.TableField<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.StudyRecord, java.math.BigInteger> ID = createField("ID", org.jooq.impl.SQLDataType.DECIMAL_INTEGER.precision(19), this);

	/**
	 * The column <code>AE2.STUDY.ACC</code>. 
	 */
	public final org.jooq.TableField<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.StudyRecord, java.lang.String> ACC = createField("ACC", org.jooq.impl.SQLDataType.VARCHAR.length(1020), this);

	/**
	 * The column <code>AE2.STUDY.DESCRIPTION</code>. 
	 */
	public final org.jooq.TableField<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.StudyRecord, java.lang.String> DESCRIPTION = createField("DESCRIPTION", org.jooq.impl.SQLDataType.CLOB, this);

	/**
	 * The column <code>AE2.STUDY.OBJECTIVE</code>. 
	 */
	public final org.jooq.TableField<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.StudyRecord, java.lang.String> OBJECTIVE = createField("OBJECTIVE", org.jooq.impl.SQLDataType.VARCHAR.length(1020), this);

	/**
	 * The column <code>AE2.STUDY.RELEASEDATE</code>. 
	 */
	public final org.jooq.TableField<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.StudyRecord, java.sql.Date> RELEASEDATE = createField("RELEASEDATE", org.jooq.impl.SQLDataType.DATE, this);

	/**
	 * The column <code>AE2.STUDY.SUBMISSIONDATE</code>. 
	 */
	public final org.jooq.TableField<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.StudyRecord, java.sql.Date> SUBMISSIONDATE = createField("SUBMISSIONDATE", org.jooq.impl.SQLDataType.DATE, this);

	/**
	 * The column <code>AE2.STUDY.TITLE</code>. 
	 */
	public final org.jooq.TableField<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.StudyRecord, java.lang.String> TITLE = createField("TITLE", org.jooq.impl.SQLDataType.VARCHAR.length(4000), this);

	/**
	 * The column <code>AE2.STUDY.LASTUPDATEDATE</code>. 
	 */
	public final org.jooq.TableField<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.StudyRecord, java.sql.Date> LASTUPDATEDATE = createField("LASTUPDATEDATE", org.jooq.impl.SQLDataType.DATE, this);

	/**
	 * Create a <code>AE2.STUDY</code> table reference
	 */
	public Study() {
		super("STUDY", uk.ac.ebi.fg.annotare2.ae.jooq.Ae2.AE2);
	}

	/**
	 * Create an aliased <code>AE2.STUDY</code> table reference
	 */
	public Study(java.lang.String alias) {
		super(alias, uk.ac.ebi.fg.annotare2.ae.jooq.Ae2.AE2, uk.ac.ebi.fg.annotare2.ae.jooq.tables.Study.STUDY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.StudyRecord> getPrimaryKey() {
		return uk.ac.ebi.fg.annotare2.ae.jooq.Keys.PK_STDY_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.StudyRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.StudyRecord>>asList(uk.ac.ebi.fg.annotare2.ae.jooq.Keys.PK_STDY_ID, uk.ac.ebi.fg.annotare2.ae.jooq.Keys.UK_STDY_ACC);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public uk.ac.ebi.fg.annotare2.ae.jooq.tables.Study as(java.lang.String alias) {
		return new uk.ac.ebi.fg.annotare2.ae.jooq.tables.Study(alias);
	}
}
