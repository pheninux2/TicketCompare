# ==========================================
# Script de requetes rapides - Production
# ShopTracker - environments/prod
# ==========================================

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   Requetes Rapides - PRODUCTION    " -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Verification que le conteneur est actif
$container = docker ps --filter "name=shoptracker-db" --format "{{.Names}}"
if (-not $container) {
    Write-Host "[ERREUR] Le conteneur shoptracker-db n'est pas demarre!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Demarrez d'abord l'application:" -ForegroundColor Yellow
    Write-Host "  cd ..\.." -ForegroundColor White
    Write-Host "  .\start-prod.ps1" -ForegroundColor White
    Write-Host ""
    exit 1
}

Write-Host "[OK] Connecte a: $container" -ForegroundColor Green
Write-Host ""

# Menu
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   Menu Principal                    " -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "  1.  Lister toutes les tables" -ForegroundColor White
Write-Host "  2.  Voir les derniers tickets (10)" -ForegroundColor White
Write-Host "  3.  Voir tous les magasins" -ForegroundColor White
Write-Host "  4.  Voir tous les utilisateurs" -ForegroundColor White
Write-Host "  5.  Statistiques par magasin" -ForegroundColor White
Write-Host "  6.  Statistiques par categorie" -ForegroundColor White
Write-Host "  7.  Taille de la base de donnees" -ForegroundColor White
Write-Host "  8.  Nombre total de tickets" -ForegroundColor White
Write-Host "  9.  Statistiques du jour" -ForegroundColor White
Write-Host "  10. Top 10 produits les plus achetes" -ForegroundColor White
Write-Host "  11. Connexions actives" -ForegroundColor White
Write-Host "  12. Taille de chaque table" -ForegroundColor White
Write-Host "  13. Executer une requete personnalisee" -ForegroundColor White
Write-Host "  0.  Quitter" -ForegroundColor White
Write-Host ""

$choix = Read-Host "Votre choix"

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan

switch ($choix) {
    "1" {
        Write-Host "   Liste des Tables" -ForegroundColor Cyan
        Write-Host "=====================================" -ForegroundColor Cyan
        Write-Host ""
        docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker -c "\dt"
    }
    "2" {
        Write-Host "   Derniers Tickets" -ForegroundColor Cyan
        Write-Host "=====================================" -ForegroundColor Cyan
        Write-Host ""
        docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker -c "SELECT id, store_id, purchase_date, total_amount FROM tickets ORDER BY purchase_date DESC LIMIT 10;"
    }
    "3" {
        Write-Host "   Liste des Magasins" -ForegroundColor Cyan
        Write-Host "=====================================" -ForegroundColor Cyan
        Write-Host ""
        docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker -c "SELECT * FROM stores ORDER BY name;"
    }
    "4" {
        Write-Host "   Liste des Utilisateurs" -ForegroundColor Cyan
        Write-Host "=====================================" -ForegroundColor Cyan
        Write-Host ""
        docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker -c "SELECT id, username, email, role, enabled, created_at FROM users ORDER BY created_at DESC;"
    }
    "5" {
        Write-Host "   Statistiques par Magasin" -ForegroundColor Cyan
        Write-Host "=====================================" -ForegroundColor Cyan
        Write-Host ""
        docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker -c "SELECT s.name, COUNT(t.id) as nb_tickets, COALESCE(SUM(t.total_amount), 0) as total FROM stores s LEFT JOIN tickets t ON t.store_id = s.id GROUP BY s.name ORDER BY total DESC;"
    }
    "6" {
        Write-Host "   Statistiques par Categorie" -ForegroundColor Cyan
        Write-Host "=====================================" -ForegroundColor Cyan
        Write-Host ""
        docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker -c "SELECT category, COUNT(*) as nb_items, SUM(total_price) as total FROM ticket_items GROUP BY category ORDER BY total DESC;"
    }
    "7" {
        Write-Host "   Taille de la Base de Donnees" -ForegroundColor Cyan
        Write-Host "=====================================" -ForegroundColor Cyan
        Write-Host ""
        docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker -c "SELECT pg_size_pretty(pg_database_size('shoptracker')) as taille;"
    }
    "8" {
        Write-Host "   Nombre Total de Tickets" -ForegroundColor Cyan
        Write-Host "=====================================" -ForegroundColor Cyan
        Write-Host ""
        docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker -c "SELECT COUNT(*) as total_tickets, COALESCE(SUM(total_amount), 0) as montant_total FROM tickets;"
    }
    "9" {
        Write-Host "   Statistiques du Jour" -ForegroundColor Cyan
        Write-Host "=====================================" -ForegroundColor Cyan
        Write-Host ""
        docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker -c "SELECT COUNT(*) as tickets_aujourdhui, COALESCE(SUM(total_amount), 0) as total FROM tickets WHERE DATE(purchase_date) = CURRENT_DATE;"
    }
    "10" {
        Write-Host "   Top 10 Produits les Plus Achetes" -ForegroundColor Cyan
        Write-Host "=====================================" -ForegroundColor Cyan
        Write-Host ""
        docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker -c "SELECT product_name, COUNT(*) as nb_achats, SUM(quantity) as quantite_totale FROM ticket_items GROUP BY product_name ORDER BY nb_achats DESC LIMIT 10;"
    }
    "11" {
        Write-Host "   Connexions Actives" -ForegroundColor Cyan
        Write-Host "=====================================" -ForegroundColor Cyan
        Write-Host ""
        docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker -c "SELECT pid, usename, application_name, client_addr, backend_start FROM pg_stat_activity WHERE datname = 'shoptracker';"
    }
    "12" {
        Write-Host "   Taille de Chaque Table" -ForegroundColor Cyan
        Write-Host "=====================================" -ForegroundColor Cyan
        Write-Host ""
        docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker -c "SELECT schemaname, tablename, pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size FROM pg_tables WHERE schemaname = 'public' ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;"
    }
    "13" {
        Write-Host "   Requete Personnalisee" -ForegroundColor Cyan
        Write-Host "=====================================" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "Entrez votre requete SQL:" -ForegroundColor Yellow
        $query = Read-Host "SQL"
        Write-Host ""
        docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker -c "$query"
    }
    "0" {
        Write-Host "   Au revoir!" -ForegroundColor Cyan
        Write-Host "=====================================" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "[OK] Script termine!" -ForegroundColor Green
        Write-Host ""
        exit 0
    }
    default {
        Write-Host "   Choix Invalide" -ForegroundColor Red
        Write-Host "=====================================" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "[ERREUR] Veuillez choisir une option valide (0-13)!" -ForegroundColor Red
        Write-Host ""
        exit 1
    }
}

Write-Host ""
Write-Host "[OK] Requete executee avec succes!" -ForegroundColor Green
Write-Host ""

