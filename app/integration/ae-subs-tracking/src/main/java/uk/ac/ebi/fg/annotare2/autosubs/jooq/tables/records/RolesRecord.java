/**
 * This class is generated by jOOQ
 */
package uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.records;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.1.0" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RolesRecord extends org.jooq.impl.UpdatableRecordImpl<uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.records.RolesRecord> implements org.jooq.Record4<java.lang.Integer, java.lang.String, java.lang.String, java.lang.Integer> {

	private static final long serialVersionUID = 599849419;

	/**
	 * Setter for <code>ae_autosubs.roles.id</code>. 
	 */
	public void setId(java.lang.Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>ae_autosubs.roles.id</code>. 
	 */
	public java.lang.Integer getId() {
		return (java.lang.Integer) getValue(0);
	}

	/**
	 * Setter for <code>ae_autosubs.roles.name</code>. 
	 */
	public void setName(java.lang.String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>ae_autosubs.roles.name</code>. 
	 */
	public java.lang.String getName() {
		return (java.lang.String) getValue(1);
	}

	/**
	 * Setter for <code>ae_autosubs.roles.info</code>. 
	 */
	public void setInfo(java.lang.String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>ae_autosubs.roles.info</code>. 
	 */
	public java.lang.String getInfo() {
		return (java.lang.String) getValue(2);
	}

	/**
	 * Setter for <code>ae_autosubs.roles.is_deleted</code>. 
	 */
	public void setIsDeleted(java.lang.Integer value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>ae_autosubs.roles.is_deleted</code>. 
	 */
	public java.lang.Integer getIsDeleted() {
		return (java.lang.Integer) getValue(3);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Record1<java.lang.Integer> key() {
		return (org.jooq.Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record4 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row4<java.lang.Integer, java.lang.String, java.lang.String, java.lang.Integer> fieldsRow() {
		return (org.jooq.Row4) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row4<java.lang.Integer, java.lang.String, java.lang.String, java.lang.Integer> valuesRow() {
		return (org.jooq.Row4) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field1() {
		return uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.Roles.ROLES.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field2() {
		return uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.Roles.ROLES.NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field3() {
		return uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.Roles.ROLES.INFO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field4() {
		return uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.Roles.ROLES.IS_DELETED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value2() {
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value3() {
		return getInfo();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value4() {
		return getIsDeleted();
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached RolesRecord
	 */
	public RolesRecord() {
		super(uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.Roles.ROLES);
	}
}
