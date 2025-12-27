-- ===================================
-- Données de test pour TicketCompare
-- À exécuter manuellement si vous voulez des données de démonstration
-- ===================================

-- Tickets de test
INSERT INTO tickets (date, store, total_amount, notes, created_at) VALUES
('2024-01-10', 'Carrefour', 45.50, 'Courses hebdomadaires', CURRENT_DATE),
('2024-01-17', 'Lidl', 32.75, 'Courses', CURRENT_DATE),
('2024-01-20', 'Monoprix', 28.90, 'Dépannage', CURRENT_DATE),
('2024-01-25', 'Carrefour', 67.20, 'Grandes courses', CURRENT_DATE);

-- Produits de test (utilisez les IDs retournés par les inserts précédents)
INSERT INTO products (name, category, price, quantity, unit, ticket_id, total_price) VALUES
('Lait Demi-Écrémé', 'Laitier', 1.50, 2, 'L', 1, 3.00),
('Pain Complet', 'Boulangerie', 2.50, 1, 'Pièce', 1, 2.50),
('Tomates', 'Fruits & Légumes', 3.20, 2, 'kg', 1, 6.40),
('Yaourt Nature', 'Laitier', 4.50, 1, 'Pack', 1, 4.50),
('Fromage Blanc', 'Laitier', 5.20, 1, 'Pot', 1, 5.20),
('Œufs', 'Œufs & Laitier', 3.80, 1, 'Boîte', 1, 3.80),
('Beurre', 'Beurre & Margarine', 4.30, 1, 'Plaquette', 1, 4.30),
('Crème Fraîche', 'Laitier', 2.90, 1, 'Pot', 1, 2.90),
('Riz Blanc', 'Féculents', 2.10, 1, 'kg', 2, 2.10),
('Pâtes', 'Féculents', 1.50, 2, 'Paquet', 2, 3.00),
('Huile d''Olive', 'Huiles', 6.50, 1, 'L', 2, 6.50),
('Sel', 'Condiments', 1.20, 1, 'Boîte', 2, 1.20),
('Poivre', 'Condiments', 3.45, 1, 'Boîte', 2, 3.45),
('Sucre', 'Sucre', 1.80, 1, 'kg', 2, 1.80),
('Café Moulu', 'Boissons', 6.20, 1, 'Paquet', 2, 6.20),
('Thé', 'Boissons', 3.50, 1, 'Boîte', 2, 3.50),
('Chocolat Noir', 'Confiserie', 2.30, 2, 'Tablette', 3, 4.60),
('Biscuits', 'Biscuiterie', 3.10, 1, 'Paquet', 3, 3.10),
('Bonbons', 'Confiserie', 2.50, 1, 'Paquet', 3, 2.50),
('Gâteau', 'Pâtisserie', 5.50, 1, 'Pièce', 3, 5.50),
('Carottes', 'Fruits & Légumes', 1.80, 3, 'kg', 3, 5.40),
('Oignons', 'Fruits & Légumes', 1.50, 2, 'kg', 3, 3.00),
('Pommes', 'Fruits & Légumes', 2.40, 2, 'kg', 3, 4.80),
('Bananes', 'Fruits & Légumes', 1.60, 1.5, 'kg', 4, 2.40),
('Raisin', 'Fruits & Légumes', 4.50, 1, 'kg', 4, 4.50),
('Orange', 'Fruits & Légumes', 3.20, 2, 'kg', 4, 6.40),
('Citron', 'Fruits & Légumes', 0.80, 4, 'Pièce', 4, 3.20),
('Viande Hachée', 'Viande', 12.50, 1, 'kg', 4, 12.50),
('Poulet Entier', 'Viande', 8.90, 1.5, 'kg', 4, 13.35),
('Saumon', 'Poisson', 15.00, 0.5, 'kg', 4, 7.50),
('Morue', 'Poisson', 12.00, 0.8, 'kg', 4, 9.60);

-- Historique des prix
INSERT INTO price_history (product_name, price, price_date, store, unit, created_at) VALUES
('Lait Demi-Écrémé', 1.50, '2024-01-10', 'Carrefour', 'L', CURRENT_DATE),
('Lait Demi-Écrémé', 1.45, '2024-01-17', 'Lidl', 'L', CURRENT_DATE),
('Lait Demi-Écrémé', 1.52, '2024-01-20', 'Monoprix', 'L', CURRENT_DATE),
('Pain Complet', 2.50, '2024-01-10', 'Carrefour', 'Pièce', CURRENT_DATE),
('Pain Complet', 2.40, '2024-01-17', 'Lidl', 'Pièce', CURRENT_DATE),
('Pain Complet', 2.70, '2024-01-20', 'Monoprix', 'Pièce', CURRENT_DATE),
('Tomates', 3.20, '2024-01-10', 'Carrefour', 'kg', CURRENT_DATE),
('Tomates', 3.50, '2024-01-17', 'Lidl', 'kg', CURRENT_DATE),
('Tomates', 3.80, '2024-01-20', 'Monoprix', 'kg', CURRENT_DATE);

