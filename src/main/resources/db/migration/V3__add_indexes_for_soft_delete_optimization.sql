CREATE INDEX idx_customer_email_active
ON customer(email)
WHERE is_deleted = false;

CREATE INDEX idx_account_iban_active
ON account(iban)
WHERE is_deleted = false;

CREATE INDEX idx_account_customer_active
ON account(customer_id)
WHERE is_deleted = false;
