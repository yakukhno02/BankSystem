CREATE TYPE account_type AS ENUM('credit', 'saving', 'deposit');

CREATE TYPE loan_status AS ENUM('active', 'closed', 'overdue');

CREATE TYPE card_type AS ENUM('debit', 'credit');

CREATE TYPE transaction_type AS ENUM('deposit', 'transfer', 'withdraw');

CREATE TABLE customer (
                          customer_id SERIAL PRIMARY KEY,
                          name VARCHAR(32) NOT NULL,
                          surname VARCHAR(32) NOT NULL,
                          email VARCHAR(64) NOT NULL UNIQUE,
                          phone_number VARCHAR(20) NOT NULL
);

CREATE TABLE account (
                         account_id SERIAL PRIMARY KEY,
                         iban VARCHAR(34) NOT NULL UNIQUE,
                         balance DECIMAL(12, 2) DEFAULT 0,
                         currency VARCHAR(3) NOT NULL,
                         type account_type NOT NULL,
                         customer_id INT NOT NULL REFERENCES customer(customer_id)
);

CREATE TABLE card (
                      card_id SERIAL PRIMARY KEY,
                      card_number VARCHAR(16) NOT NULL UNIQUE,
                      expiration_date DATE NOT NULL,
                      type card_type NOT NULL,
                      account_id INT NOT NULL REFERENCES account(account_id)
);

CREATE TABLE transactions (
                              transaction_id SERIAL PRIMARY KEY,
                              type transaction_type NOT NULL,
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
                      status loan_status NOT NULL,
                      account_id INT NOT NULL REFERENCES account(account_id)
);