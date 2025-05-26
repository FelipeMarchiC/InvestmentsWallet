DROP TABLE IF EXISTS app_user;
DROP TABLE IF EXISTS asset;
DROP TABLE IF EXISTS investment;
DROP TABLE IF EXISTS wallet;

CREATE TABLE app_user (
    id           VARCHAR(36)    PRIMARY KEY,
    email        VARCHAR(255)   NOT NULL,
    lastname     VARCHAR(255)   NOT NULL,
    name         VARCHAR(255)   NOT NULL,
    password     VARCHAR(255)   NOT NULL,
    role         VARCHAR(255)   CHECK (role IN ('USER','ADMIN'))
);

CREATE TABLE asset (
    maturity_date DATE,
    profitability FLOAT        NOT NULL,
    id             VARCHAR(36) PRIMARY KEY,
    asset_type     VARCHAR(255)CHECK (asset_type IN ('TESOURO_DIRETO','CDB','LCI','LCA','CRI','CRA')),
    name          VARCHAR(255)
);

CREATE TABLE investment (
    initial_value  FLOAT,
    purchase_date  DATE,
    withdraw_date  DATE,
    asset_id       VARCHAR(36) NOT NULL,
    id             VARCHAR(36) PRIMARY KEY,
    wallet_id      VARCHAR(36)
);

CREATE TABLE wallet (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL UNIQUE,
    FOREIGN KEY (user_id)
        REFERENCES app_user(id)
        ON DELETE CASCADE
);

INSERT INTO asset (id, name, asset_type, profitability, maturity_date)
VALUES
    ('5bbff5c5-e4df-4e37-9f46-5cdc332f1f70',
     'Tesouro Selic 2025',
     'TESOURO_DIRETO',
     0.065,
     STRFTIME('%Y-%m-%d %H:%M:%f', 'now', '+1 year')
    ),
    ('cd63e59b-1fbf-4461-a03e-8d3449610b14',
     'CDB Banco Inter',
     'CDB',
     0.12,
     STRFTIME('%Y-%m-%d %H:%M:%f', 'now', '+1 year', '+3 months')
    ),
    ('5ddc3ad6-5ec3-406b-a8dc-b101c88b46b9',
     'LCI Banco do Brasil',
     'LCI',
     0.10,
     STRFTIME('%Y-%m-%d %H:%M:%f', 'now', '+2 years')
    ),
    ('4fbd0846-dc52-4de7-b08f-85d0fe321bac',
     'LCA Caixa Econômica',
     'LCA',
     0.095,
     STRFTIME('%Y-%m-%d %H:%M:%f', 'now', '+1 year', '+6 months')
    ),
    ('6377a1e9-617f-44da-af38-80957273854d',
     'CRI Imóvel SP',
     'CRI',
     0.13,
     STRFTIME('%Y-%m-%d %H:%M:%f', 'now', '+3 years')
    ),
    ('8222cddd-2445-42dc-9a60-39c46bbc169c',
     'CRA Agronegócio MT',
     'CRA',
     0.14,
     STRFTIME('%Y-%m-%d %H:%M:%f', 'now', '+2 years', '+6 months')
);
