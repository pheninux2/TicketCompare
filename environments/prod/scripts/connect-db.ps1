# ==========================================
# Script d'accès à PostgreSQL - Production
# ShopTracker - environments/prod
# ==========================================

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   Accès PostgreSQL - PRODUCTION    " -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Vérification que le conteneur est actif
Write-Host "[*] Verification du conteneur PostgreSQL..." -ForegroundColor Yellow
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

Write-Host "[OK] Conteneur actif: $container" -ForegroundColor Green
Write-Host ""

# Informations de connexion
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   Informations de Connexion        " -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Base de donnees : shoptracker" -ForegroundColor White
Write-Host "  Utilisateur     : shoptracker_admin" -ForegroundColor White
Write-Host "  Mot de passe    : ShopTracker2025!Secure" -ForegroundColor White
Write-Host "  Environnement   : PRODUCTION" -ForegroundColor Yellow
Write-Host ""

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   Commandes psql Utiles            " -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "  \dt              - Lister les tables" -ForegroundColor White
Write-Host "  \d nom_table     - Decrire une table" -ForegroundColor White
Write-Host "  \l               - Lister les bases de donnees" -ForegroundColor White
Write-Host "  \du              - Lister les utilisateurs" -ForegroundColor White
Write-Host "  \q               - Quitter" -ForegroundColor White
Write-Host ""
Write-Host "  SELECT * FROM tickets LIMIT 10;    - Voir les tickets" -ForegroundColor White
Write-Host "  SELECT * FROM stores;              - Voir les magasins" -ForegroundColor White
Write-Host "  SELECT * FROM users;               - Voir les utilisateurs" -ForegroundColor White
Write-Host ""

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   Connexion en cours...             " -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Connexion à PostgreSQL
docker exec -it shoptracker-db psql -U shoptracker_admin -d shoptracker

Write-Host ""
Write-Host "[OK] Deconnexion reussie!" -ForegroundColor Green
Write-Host ""

