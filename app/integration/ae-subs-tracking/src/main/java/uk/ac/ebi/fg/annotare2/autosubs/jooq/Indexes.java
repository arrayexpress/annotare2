/*
 * This file is generated by jOOQ.
 */
package uk.ac.ebi.fg.annotare2.autosubs.jooq;


import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;

import uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.DataFiles;
import uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.Experiments;
import uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.RolesUsers;
import uk.ac.ebi.fg.annotare2.autosubs.jooq.tables.Spreadsheets;


/**
 * A class modelling indexes of tables in ae_autosubs.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index DATA_FILES_EXPERIMENT_ID = Internal.createIndex(DSL.name("experiment_id"), DataFiles.DATA_FILES, new OrderField[] { DataFiles.DATA_FILES.EXPERIMENT_ID }, false);
    public static final Index SPREADSHEETS_EXPERIMENT_ID = Internal.createIndex(DSL.name("experiment_id"), Spreadsheets.SPREADSHEETS, new OrderField[] { Spreadsheets.SPREADSHEETS.EXPERIMENT_ID }, false);
    public static final Index ROLES_USERS_ROLE_ID = Internal.createIndex(DSL.name("role_id"), RolesUsers.ROLES_USERS, new OrderField[] { RolesUsers.ROLES_USERS.ROLE_ID }, false);
    public static final Index EXPERIMENTS_USER_ID = Internal.createIndex(DSL.name("user_id"), Experiments.EXPERIMENTS, new OrderField[] { Experiments.EXPERIMENTS.USER_ID }, false);
    public static final Index ROLES_USERS_USER_ID = Internal.createIndex(DSL.name("user_id"), RolesUsers.ROLES_USERS, new OrderField[] { RolesUsers.ROLES_USERS.USER_ID }, false);
}
