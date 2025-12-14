ALTER TABLE customer
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE account
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX idx_customer_is_deleted ON customer(is_deleted);
CREATE INDEX idx_account_is_deleted ON account(is_deleted);
