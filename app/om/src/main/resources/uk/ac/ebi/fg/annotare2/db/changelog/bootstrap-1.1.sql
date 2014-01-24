/* INSERT DEFAULT USER */

INSERT INTO users (name, email, password, emailVerified) VALUES ('Annotare curator', 'curator@ebi.ac.uk', 'b06bb1bec903b5f87a7a482cd8385267', 1);
SET @userId = LAST_INSERT_ID();

INSERT INTO user_roles (user, role) VALUES (@userId, 'AUTHENTICATED');
INSERT INTO user_roles (user, role) VALUES (@userId, 'CURATOR');

/* INSERT SUBMISSION ACL */

INSERT INTO acl (aclType) VALUES ('SUBMISSION');
SET @aclId = LAST_INSERT_ID();

INSERT INTO acl_entries (acl, role, permission) VALUES (@aclId, 'AUTHENTICATED', 'CREATE');
INSERT INTO acl_entries (acl, role, permission) VALUES (@aclId, 'CREATOR', 'VIEW');
INSERT INTO acl_entries (acl, role, permission) VALUES (@aclId, 'OWNER', 'VIEW');
INSERT INTO acl_entries (acl, role, permission) VALUES (@aclId, 'OWNER', 'UPDATE');
INSERT INTO acl_entries (acl, role, permission) VALUES (@aclId, 'CURATOR', 'VIEW');
INSERT INTO acl_entries (acl, role, permission) VALUES (@aclId, 'CURATOR', 'UPDATE');