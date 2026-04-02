-- Data migration not performed: existing income/expense records lack user_id,
-- which is required (NOT NULL) in the new transactions table for multi-tenancy.
DROP TABLE IF EXISTS income;
DROP TABLE IF EXISTS expense;
