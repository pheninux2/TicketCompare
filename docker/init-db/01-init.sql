-- ===================================
-- Script d'initialisation PostgreSQL
-- TicketCompare Database
-- ===================================

-- Créer les extensions nécessaires
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Configuration locale française
SET TIME ZONE 'Europe/Paris';

-- Message de confirmation
DO $$
BEGIN
    RAISE NOTICE 'Base de données TicketCompare initialisée avec succès!';
    RAISE NOTICE 'Extensions installées: uuid-ossp, pg_trgm';
    RAISE NOTICE 'Fuseau horaire: Europe/Paris';
END $$;

