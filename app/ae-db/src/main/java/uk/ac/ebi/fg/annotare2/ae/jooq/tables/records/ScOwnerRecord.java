/**
 * This class is generated by jOOQ
 */
package uk.ac.ebi.fg.annotare2.ae.jooq.tables.records;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.1.0" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ScOwnerRecord extends org.jooq.impl.UpdatableRecordImpl<uk.ac.ebi.fg.annotare2.ae.jooq.tables.records.ScOwnerRecord> implements org.jooq.Record3<java.math.BigInteger, java.math.BigInteger, java.math.BigInteger> {

	private static final long serialVersionUID = -1132624259;

	/**
	 * Setter for <code>AE2.SC_OWNER.ID</code>. 
	 */
	public void setId(java.math.BigInteger value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>AE2.SC_OWNER.ID</code>. 
	 */
	public java.math.BigInteger getId() {
		return (java.math.BigInteger) getValue(0);
	}

	/**
	 * Setter for <code>AE2.SC_OWNER.SC_LABEL_ID</code>. 
	 */
	public void setScLabelId(java.math.BigInteger value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>AE2.SC_OWNER.SC_LABEL_ID</code>. 
	 */
	public java.math.BigInteger getScLabelId() {
		return (java.math.BigInteger) getValue(1);
	}

	/**
	 * Setter for <code>AE2.SC_OWNER.SC_USER_ID</code>. 
	 */
	public void setScUserId(java.math.BigInteger value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>AE2.SC_OWNER.SC_USER_ID</code>. 
	 */
	public java.math.BigInteger getScUserId() {
		return (java.math.BigInteger) getValue(2);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Record1<java.math.BigInteger> key() {
		return (org.jooq.Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record3 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row3<java.math.BigInteger, java.math.BigInteger, java.math.BigInteger> fieldsRow() {
		return (org.jooq.Row3) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row3<java.math.BigInteger, java.math.BigInteger, java.math.BigInteger> valuesRow() {
		return (org.jooq.Row3) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.math.BigInteger> field1() {
		return uk.ac.ebi.fg.annotare2.ae.jooq.tables.ScOwner.SC_OWNER.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.math.BigInteger> field2() {
		return uk.ac.ebi.fg.annotare2.ae.jooq.tables.ScOwner.SC_OWNER.SC_LABEL_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.math.BigInteger> field3() {
		return uk.ac.ebi.fg.annotare2.ae.jooq.tables.ScOwner.SC_OWNER.SC_USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.math.BigInteger value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.math.BigInteger value2() {
		return getScLabelId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.math.BigInteger value3() {
		return getScUserId();
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached ScOwnerRecord
	 */
	public ScOwnerRecord() {
		super(uk.ac.ebi.fg.annotare2.ae.jooq.tables.ScOwner.SC_OWNER);
	}
}
