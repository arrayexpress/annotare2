CREATE TABLE users (
  id       BIGINT AUTO_INCREMENT NOT NULL,
  email    VARCHAR(80),
  password VARCHAR(255),
  PRIMARY KEY (id),
  CONSTRAINT unique_email UNIQUE (email)
)
  ENGINE = InnoDB;

CREATE TABLE user_roles (
  id   BIGINT AUTO_INCREMENT                              NOT NULL,
  user BIGINT                                             NOT NULL,
  role ENUM('OWNER', 'AUTHENTICATED', 'CURATOR', 'ADMIN') NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT FOREIGN KEY (user) REFERENCES users (id)
    ON DELETE CASCADE,
  CONSTRAINT unique_user_role UNIQUE (user, role)
)
  ENGINE = InnoDB;

CREATE TABLE acl (
  id      BIGINT AUTO_INCREMENT NOT NULL,
  aclType ENUM('SUBMISSION')    NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT unique_acl_type UNIQUE (aclType)
)
  ENGINE = InnoDB;

CREATE TABLE acl_entries (
  id         BIGINT AUTO_INCREMENT                              NOT NULL,
  acl        BIGINT                                             NOT NULL,
  role       ENUM('OWNER', 'AUTHENTICATED', 'CURATOR', 'ADMIN') NOT NULL,
  permission ENUM ('CREATE', 'UPDATE', 'VIEW')                  NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT FOREIGN KEY (acl) REFERENCES acl (id)
    ON DELETE CASCADE,
  CONSTRAINT unique_acl_role_permission UNIQUE (acl, role, permission)
)
  ENGINE = InnoDB;

CREATE TABLE submissions (
  id                BIGINT AUTO_INCREMENT              NOT NULL,
  created           TIMESTAMP                          NOT NULL,
  status            ENUM('IN_PROGRESS',
                         'SUBMITTED',
                         'IN_CURATION',
                         'PRIVATE_IN_AE',
                         'PUBLIC_IN_AE')               NOT NULL,
  type              ENUM('EXPERIMENT', 'ARRAY_DESIGN') NOT NULL,
  accession         VARCHAR(50),
  title             VARCHAR(500),
  acl               BIGINT,
  createdBy         BIGINT                             NOT NULL,
  experiment        MEDIUMTEXT,
  arrayDesignHeader TEXT,
  arrayDesignBody   MEDIUMTEXT,
  PRIMARY KEY (id),
  CONSTRAINT FOREIGN KEY (acl) REFERENCES acl (id),
  CONSTRAINT FOREIGN KEY (createdBy) REFERENCES users (id)
    ON DELETE CASCADE
)
  ENGINE = InnoDB;

CREATE TABLE data_files (
  id       BIGINT AUTO_INCREMENT                   NOT NULL,
  created  TIMESTAMP                               NOT NULL,
  status   ENUM('TO_BE_STORED', 'STORED', 'ERROR') NOT NULL,
  fileName VARCHAR(255)                            NOT NULL,
  digest   VARCHAR(255),
  ownedBy  BIGINT                                  NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT FOREIGN KEY (ownedBy) REFERENCES submissions (id)
    ON DELETE CASCADE
)
  ENGINE =InnoDB;