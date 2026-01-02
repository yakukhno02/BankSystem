CREATE TABLE customer (
                          customer_id SERIAL PRIMARY KEY,
                          name VARCHAR(32) NOT NULL,
                          surname VARCHAR(32) NOT NULL,
                          email VARCHAR(64) NOT NULL UNIQUE,
                          phone_number VARCHAR(20) NOT NULL UNIQUE,
                          status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                          CHECK (status IN ('ACTIVE', 'BLOCKED', 'CLOSED'))
);

CREATE TABLE account (
                         account_id SERIAL PRIMARY KEY,
                         iban VARCHAR(34) NOT NULL UNIQUE,
                         balance DECIMAL(12, 2) NOT NULL DEFAULT 0,
                         currency VARCHAR(3) NOT NULL,
                         type VARCHAR(20) NOT NULL CHECK (type IN ('CREDIT', 'SAVING', 'DEPOSIT')),
                         customer_id INT NOT NULL REFERENCES customer(customer_id),
                         status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                         CHECK (status IN ('ACTIVE', 'CLOSED', 'FROZEN'))
);

CREATE TABLE card (
                      card_id SERIAL PRIMARY KEY,
                      card_number VARCHAR(16) NOT NULL UNIQUE,
                      expiration_date DATE NOT NULL,
                      type VARCHAR(20) NOT NULL CHECK (type IN ('DEBIT', 'CREDIT')),
                      status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                      CHECK (status IN ('ACTIVE', 'BLOCKED', 'CLOSED')),
                      account_id INT NOT NULL REFERENCES account(account_id)
);

CREATE TABLE transactions (
                              transaction_id SERIAL PRIMARY KEY,
                              type VARCHAR(20) NOT NULL CHECK (type IN ('DEPOSIT', 'TRANSFER', 'WITHDRAW')),
                              currency VARCHAR(3) NOT NULL,
                              amount DECIMAL(12, 2) NOT NULL CHECK (amount > 0),
                              date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              description VARCHAR(255),
                              from_account_id INT REFERENCES account(account_id),
                              to_account_id INT REFERENCES account(account_id)
);

CREATE TABLE loan (
                      loan_id SERIAL PRIMARY KEY,
                      amount DECIMAL(12, 2) NOT NULL,
                      interest_rate DECIMAL(5, 2) CHECK (interest_rate >= 0 AND interest_rate <= 100),
                      start_date DATE NOT NULL,
                      end_date DATE NOT NULL,
                      status VARCHAR(20) NOT NULL CHECK(status IN ('ACTIVE', 'CLOSED', 'OVERDUE')),
                      account_id INT NOT NULL REFERENCES account(account_id)
);