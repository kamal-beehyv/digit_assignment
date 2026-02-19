-- Run this in the same database used by egov-idgen (e.g. digit_assignment).
-- Required for advocate application number generation (IdFormat from MDMS).
CREATE SEQUENCE IF NOT EXISTS "SEQ_EG_ADVOCATE_APPLICATION_NUM";
