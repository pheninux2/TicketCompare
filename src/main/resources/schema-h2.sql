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

