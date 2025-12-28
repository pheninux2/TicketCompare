-- Schéma H2 pour TicketCompare
-- Optimisé pour H2 Database

-- Table des Tickets
CREATE TABLE IF NOT EXISTS tickets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    date DATE NOT NULL,
    store VARCHAR(100) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    notes VARCHAR(255),
    created_at DATE NOT NULL,
    CONSTRAINT idx_ticket_date UNIQUE (id, date),
    CONSTRAINT idx_ticket_store UNIQUE (id, store)
);

-- Table des Produits
CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(100),
    price DECIMAL(10, 2) NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL,
    unit VARCHAR(50),
    ticket_id BIGINT NOT NULL,
    description VARCHAR(255),
    total_price DECIMAL(10, 2),
    FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    CONSTRAINT idx_product_name UNIQUE (id, name),
    CONSTRAINT idx_product_category UNIQUE (id, category),
    CONSTRAINT idx_product_ticket UNIQUE (id, ticket_id)
);

-- Table d'Historique des Prix
CREATE TABLE IF NOT EXISTS price_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(200) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    price_date DATE NOT NULL,
    store VARCHAR(100),
    unit VARCHAR(50),
    created_at DATE NOT NULL,
    CONSTRAINT idx_price_product_date UNIQUE (id, product_name, price_date),
    CONSTRAINT idx_price_date UNIQUE (id, price_date)
);

-- Table des Statistiques de Consommation
CREATE TABLE IF NOT EXISTS consumption_statistics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(200) NOT NULL,
    week_start DATE NOT NULL,
    total_quantity DECIMAL(10, 2) NOT NULL,
    total_cost DECIMAL(10, 2) NOT NULL,
    purchase_count BIGINT NOT NULL,
    category VARCHAR(100),
    unit VARCHAR(50),
    created_at DATE NOT NULL,
    CONSTRAINT idx_consumption_product_week UNIQUE (id, product_name, week_start),
    CONSTRAINT idx_consumption_week UNIQUE (id, week_start)
);

-- Table des Snapshots de Statistiques
CREATE TABLE IF NOT EXISTS statistic_snapshots (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category VARCHAR(100) NOT NULL,
    min_price DECIMAL(10, 2) NOT NULL,
    max_price DECIMAL(10, 2) NOT NULL,
    average_price DECIMAL(10, 2) NOT NULL,
    product_count BIGINT NOT NULL,
    snapshot_date DATE NOT NULL,
    created_at DATE NOT NULL,
    CONSTRAINT idx_snapshot_category UNIQUE (id, category),
    CONSTRAINT idx_snapshot_date UNIQUE (id, snapshot_date)
);

-- ============================================
-- SYSTÈME DE GESTION DES LICENCES
-- ============================================

-- Table des Utilisateurs
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verification_token VARCHAR(255) UNIQUE,
    verification_token_expiry TIMESTAMP,
    CONSTRAINT idx_user_email UNIQUE (id, email)
);

-- Table des Licences
CREATE TABLE IF NOT EXISTS licenses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    license_type VARCHAR(20) NOT NULL,
    license_key VARCHAR(100) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    expiry_date DATE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    auto_renew BOOLEAN NOT NULL DEFAULT FALSE,
    notes VARCHAR(500),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT idx_license_user UNIQUE (id, user_id),
    CONSTRAINT idx_license_key UNIQUE (id, license_key)
);

-- Index pour optimiser les recherches de licences
CREATE INDEX IF NOT EXISTS idx_license_status ON licenses(status);
CREATE INDEX IF NOT EXISTS idx_license_expiry ON licenses(expiry_date);
CREATE INDEX IF NOT EXISTS idx_license_type ON licenses(license_type);

-- Table des Abonnements (Subscriptions)
CREATE TABLE IF NOT EXISTS subscriptions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    plan_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    start_date DATE NOT NULL,
    next_billing_date DATE,
    end_date DATE,
    auto_renew BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    payment_method VARCHAR(100),
    payment_reference VARCHAR(200),
    notes VARCHAR(500),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT idx_subscription_user UNIQUE (id, user_id)
);

-- Index pour optimiser les recherches d'abonnements
CREATE INDEX IF NOT EXISTS idx_subscription_status ON subscriptions(status);
CREATE INDEX IF NOT EXISTS idx_subscription_billing ON subscriptions(next_billing_date);
CREATE INDEX IF NOT EXISTS idx_subscription_user_status ON subscriptions(user_id, status);

