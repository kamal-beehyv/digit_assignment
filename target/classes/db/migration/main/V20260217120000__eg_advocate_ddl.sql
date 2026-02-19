-- Advocate Registry (digit_assignment) - main table
CREATE TABLE eg_advocate (
    id character varying(64) NOT NULL,
    tenantid character varying(64) NOT NULL,
    applicationnumber character varying(128),
    barregistrationnumber character varying(128),
    advocatetype character varying(64),
    organisationid character varying(64),
    individualid character varying(64),
    isactive boolean DEFAULT true,
    createdby character varying(64),
    lastmodifiedby character varying(64),
    createdtime bigint,
    lastmodifiedtime bigint,
    CONSTRAINT pk_eg_advocate PRIMARY KEY (id),
    CONSTRAINT uk_eg_advocate_appno UNIQUE (tenantid, applicationnumber)
);

CREATE INDEX idx_eg_advocate_tenant ON eg_advocate(tenantid);
CREATE INDEX idx_eg_advocate_appno ON eg_advocate(applicationnumber);
CREATE INDEX idx_eg_advocate_bar ON eg_advocate(barregistrationnumber);
