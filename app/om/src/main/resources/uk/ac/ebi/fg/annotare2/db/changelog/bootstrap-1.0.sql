/* INSERT DEFAULT USER */

INSERT INTO users (email, password) VALUES ('user@ebi.ac.uk', 'ee11cbb19052e40b07aac0ca060c23ee');
SET @userId = LAST_INSERT_ID();

INSERT INTO user_roles (user, role) VALUES (@userId, 'AUTHENTICATED');

/* INSERT SUBMISSION ACL */

INSERT INTO acl (aclType) VALUES ('SUBMISSION');
SET @aclId = LAST_INSERT_ID();

INSERT INTO acl_entries (acl, role, permission) VALUES (@aclId, 'AUTHENTICATED', 'CREATE');
INSERT INTO acl_entries (acl, role, permission) VALUES (@aclId, 'OWNER', 'VIEW');
INSERT INTO acl_entries (acl, role, permission) VALUES (@aclId, 'OWNER', 'UPDATE');
INSERT INTO acl_entries (acl, role, permission) VALUES (@aclId, 'CURATOR', 'VIEW');
INSERT INTO acl_entries (acl, role, permission) VALUES (@aclId, 'CURATOR', 'UPDATE');