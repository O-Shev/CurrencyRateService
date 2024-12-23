CREATE TABLE currency_rate (
    id BIGSERIAL PRIMARY KEY,
    currency VARCHAR(3) NOT NULL,
    rate NUMERIC(19, 4) NOT NULL,
    type VARCHAR(10) NOT NULL,
    last_updated TIMESTAMP NOT NULL,
    CONSTRAINT unique_currency_type UNIQUE (currency, type)
);
