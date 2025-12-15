CREATE INDEX idx_customer_email_active
ON customer(email)
WHERE is_deleted = false;

CREATE INDEX idx_account_iban_active
ON account(iban)
WHERE is_deleted = false;

CREATE INDEX idx_account_customer_active
ON account(customer_id)
WHERE is_deleted = false;

CREATE INDEX idx_card_account
ON card(account_id);

CREATE INDEX idx_transaction_from_account
ON transactions(from_account_id);

CREATE INDEX idx_transaction_to_account
ON transactions(to_account_id);
