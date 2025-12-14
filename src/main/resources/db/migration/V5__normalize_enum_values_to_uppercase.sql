ALTER TABLE account
DROP CONSTRAINT IF EXISTS account_type_check;

UPDATE account
SET type = UPPER(type);

ALTER TABLE account
ADD CONSTRAINT account_type_check
CHECK (type IN ('CREDIT', 'SAVING', 'DEPOSIT'));

ALTER TABLE transactions
DROP CONSTRAINT IF EXISTS transaction_type_check;

UPDATE transactions
SET type = UPPER(type);

ALTER TABLE transactions
ADD CONSTRAINT transaction_type_check
CHECK (type IN ('TRANSFER', 'WITHDRAW', 'DEPOSIT'));

ALTER TABLE loan
DROP CONSTRAINT IF EXISTS loan_status_check;

UPDATE loan
SET status = UPPER(status);

ALTER TABLE loan
ADD CONSTRAINT loan_status_check
CHECK (status IN ('ACTIVE', 'CLOSED', 'OVERDUE'));

