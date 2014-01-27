CREATE TABLE users (
  id                      BIGINT AUTO_INCREMENT NOT NULL,
  name                    VARCHAR(255)          NOT NULL,
  email                   VARCHAR(80),
  emailVerified           TINYINT(1)            NOT NULL
                            DEFAULT 0,
  password                VARCHAR(255),
  passwordChangeRequested TINYINT(1)            NOT NULL
                            DEFAULT 0,
  verificationToken       VARCHAR(255),
  PRIMARY KEY (id),
  CONSTRAINT unique_email UNIQUE (email)
)
  ENGINE = InnoDB DEFAULT CHARSET = UTF8;

CREATE TABLE user_roles (
  id   BIGINT AUTO_INCREMENT NOT NULL,
  user BIGINT                NOT NULL,
  role ENUM('CREATOR',
            'OWNER',
            'AUTHENTICATED',
            'CURATOR',
            'ADMIN')         NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT FOREIGN KEY (user) REFERENCES users (id)
    ON DELETE CASCADE,
  CONSTRAINT unique_user_role UNIQUE (user, role)
)
  ENGINE = InnoDB DEFAULT CHARSET = UTF8;

CREATE TABLE acl (
  id      BIGINT AUTO_INCREMENT NOT NULL,
  aclType ENUM('SUBMISSION')    NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT unique_acl_type UNIQUE (aclType)
)
  ENGINE = InnoDB DEFAULT CHARSET = UTF8;

CREATE TABLE acl_entries (
  id         BIGINT AUTO_INCREMENT             NOT NULL,
  acl        BIGINT                            NOT NULL,
  role       ENUM('CREATOR',
                  'OWNER',
                  'AUTHENTICATED',
                  'CURATOR',
                  'ADMIN')                     NOT NULL,
  permission ENUM ('CREATE', 'UPDATE', 'VIEW') NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT FOREIGN KEY (acl) REFERENCES acl (id)
    ON DELETE CASCADE,
  CONSTRAINT unique_acl_role_permission UNIQUE (acl, role, permission)
)
  ENGINE = InnoDB DEFAULT CHARSET = UTF8;

CREATE TABLE submissions (
  id                BIGINT AUTO_INCREMENT              NOT NULL,
  created           TIMESTAMP                          NOT NULL
                      DEFAULT '0000-00-00 00:00:00',
  updated           TIMESTAMP                          NOT NULL
                      DEFAULT CURRENT_TIMESTAMP
                      ON UPDATE CURRENT_TIMESTAMP,
  status            ENUM('IN_PROGRESS',
                         'SUBMITTED',
                         'IN_CURATION',
                         'PRIVATE_IN_AE',
                         'PUBLIC_IN_AE')               NOT NULL,
  type              ENUM('EXPERIMENT', 'ARRAY_DESIGN') NOT NULL,
  accession         VARCHAR(255),
  title             VARCHAR(255),
  acl               BIGINT,
  createdBy         BIGINT                             NOT NULL,
  ownedBy           BIGINT                             NOT NULL,
  experiment        MEDIUMTEXT,
  arrayDesignHeader TEXT,
  arrayDesignBody   MEDIUMTEXT,
  subsTrackingId    INT,
  deleted           TINYINT(1)                         NOT NULL
                      DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT FOREIGN KEY (acl) REFERENCES acl (id),
  CONSTRAINT FOREIGN KEY (createdBy) REFERENCES users (id)
    ON DELETE CASCADE,
  CONSTRAINT FOREIGN KEY (ownedBy) REFERENCES users (id)
)
  ENGINE = InnoDB DEFAULT CHARSET = UTF8;

CREATE TABLE data_files (
  id       BIGINT AUTO_INCREMENT                   NOT NULL,
  created  TIMESTAMP                               NOT NULL,
  status   ENUM('TO_BE_STORED', 'STORED', 'ERROR') NOT NULL,
  fileName VARCHAR(255)                            NOT NULL,
  digest   VARCHAR(255),
  ownedBy  BIGINT                                  NOT NULL,
  deleted  TINYINT(1)                              NOT NULL
             DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT FOREIGN KEY (ownedBy) REFERENCES submissions (id)
    ON DELETE CASCADE
)
  ENGINE = InnoDB DEFAULT CHARSET = UTF8;