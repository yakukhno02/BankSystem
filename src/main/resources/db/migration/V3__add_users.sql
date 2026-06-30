CREATE TABLE app_user (
                          user_id SERIAL PRIMARY KEY,
                          email VARCHAR(64) NOT NULL UNIQUE,
                          password_hash VARCHAR(255) NOT NULL,
                          role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER'
                              CHECK (role IN ('CUSTOMER', 'ADMIN')),
                          enabled BOOLEAN NOT NULL DEFAULT TRUE,
                          account_locked BOOLEAN NOT NULL DEFAULT FALSE,
                          customer_id INT UNIQUE REFERENCES customer(customer_id)
);

CREATE INDEX idx_app_user_email ON app_user(email);
