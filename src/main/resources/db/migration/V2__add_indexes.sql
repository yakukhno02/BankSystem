CREATE INDEX idx_account_customer_active
ON account(customer_id)
WHERE status = 'ACTIVE';

CREATE INDEX idx_card_account
ON card(account_id);

CREATE INDEX idx_transaction_from_account
ON transactions(from_account_id);

CREATE INDEX idx_transaction_to_account
ON transactions(to_account_id);
