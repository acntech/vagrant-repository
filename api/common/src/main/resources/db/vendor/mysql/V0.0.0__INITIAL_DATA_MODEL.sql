-- noinspection SqlNoDataSourceInspectionForFile

CREATE TABLE GROUPS
( ID                                        INT(19)                   NOT NULL AUTO_INCREMENT
, NAME                                      VARCHAR(50)               NOT NULL
, DESCRIPTION                               VARCHAR(4000)
, CREATED                                   TIMESTAMP(6)              NOT NULL DEFAULT CURRENT_TIMESTAMP
, MODIFIED                                  TIMESTAMP(6)              NOT NULL DEFAULT CURRENT_TIMESTAMP
, CONSTRAINT GROUPS_PK                      PRIMARY KEY (ID)
, CONSTRAINT GROUPS_NAME_UC                 UNIQUE (NAME)
);

CREATE TABLE BOXES
( ID                                        INT(19)                   NOT NULL AUTO_INCREMENT
, NAME                                      VARCHAR(50)               NOT NULL
, DESCRIPTION                               VARCHAR(4000)
, GROUP_ID                                  INT(19)                   NOT NULL
, CREATED                                   TIMESTAMP(6)              NOT NULL DEFAULT CURRENT_TIMESTAMP
, MODIFIED                                  TIMESTAMP(6)              NOT NULL DEFAULT CURRENT_TIMESTAMP
, CONSTRAINT BOXES_PK                       PRIMARY KEY (ID)
, CONSTRAINT BOXES_NAME_GROUP_ID_UC         UNIQUE (NAME, GROUP_ID)
, CONSTRAINT GROUP_ID_FK                    FOREIGN KEY (GROUP_ID)    REFERENCES GROUPS (ID)
);

CREATE TABLE VERSIONS
( ID                                        INT(19)                   NOT NULL AUTO_INCREMENT
, NAME                                      VARCHAR(50)               NOT NULL
, DESCRIPTION                               VARCHAR(4000)
, BOX_ID                                    INT(19)                   NOT NULL
, CREATED                                   TIMESTAMP(6)              NOT NULL DEFAULT CURRENT_TIMESTAMP
, MODIFIED                                  TIMESTAMP(6)              NOT NULL DEFAULT CURRENT_TIMESTAMP
, CONSTRAINT VERSIONS_PK                    PRIMARY KEY (ID)
, CONSTRAINT VERSIONS_NAME_BOX_ID_UC        UNIQUE (NAME, BOX_ID)
, CONSTRAINT BOX_ID_FK                      FOREIGN KEY (BOX_ID)      REFERENCES BOXES (ID)
);

CREATE TABLE PROVIDERS
( ID                                        INT(19)                   NOT NULL AUTO_INCREMENT
, PROVIDER_TYPE                             VARCHAR(50)               NOT NULL
, SIZE                                      INT(19)
, CHECKSUM_TYPE                             VARCHAR(10)
, CHECKSUM                                  VARCHAR(100)
, VERSION_ID                                INT(19)                   NOT NULL
, CREATED                                   TIMESTAMP(6)              NOT NULL DEFAULT CURRENT_TIMESTAMP
, MODIFIED                                  TIMESTAMP(6)              NOT NULL DEFAULT CURRENT_TIMESTAMP
, CONSTRAINT PROVIDERS_PK                   PRIMARY KEY (ID)
, CONSTRAINT PROVIDERS_NAME_VERSION_ID_UC   UNIQUE (PROVIDER_TYPE, VERSION_ID)
, CONSTRAINT VERSION_ID_FK                  FOREIGN KEY (VERSION_ID)  REFERENCES VERSIONS (ID)
);
