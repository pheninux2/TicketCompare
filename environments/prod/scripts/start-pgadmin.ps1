# ==========================================
# Script PgAdmin - Production
# ShopTracker - environments/prod
# Demarrage de l'interface d'administration
# ==========================================

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   PgAdmin - Interface Web          " -ForegroundColor Cyan
Write-Host "   Environnement: PRODUCTION        " -ForegroundColor Yellow
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Vérifier si PgAdmin est déjà démarré
$pgadminRunning = docker ps --filter "name=shoptracker-pgadmin" --format "{{.Names}}"

if ($pgadminRunning) {
    Write-Host "[OK] PgAdmin est deja demarre!" -ForegroundColor Green
    Write-Host ""
} else {
    Write-Host "[*] Demarrage de PgAdmin..." -ForegroundColor Yellow

    # Aller dans le répertoire prod
    $currentDir = Get-Location
    Set-Location -Path ".."

    # Démarrer PgAdmin avec le profil admin
    docker-compose --profile admin up -d

    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] PgAdmin demarre avec succes!" -ForegroundColor Green
    } else {
        Write-Host "[ERREUR] Echec du demarrage de PgAdmin!" -ForegroundColor Red
        Set-Location -Path $currentDir
        exit 1
    }

    Write-Host ""

    # Retour au répertoire scripts
    Set-Location -Path "scripts"
}

# Informations de connexion
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   Acces a PgAdmin                  " -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "URL           : " -NoNewline -ForegroundColor White
Write-Host "http://localhost:5050" -ForegroundColor Green
Write-Host "Email         : " -NoNewline -ForegroundColor White
Write-Host "admin@shoptracker.local" -ForegroundColor Green
Write-Host "Mot de passe  : " -NoNewline -ForegroundColor White
Write-Host "AdminSecure2025!" -ForegroundColor Green
Write-Host ""

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   Configuration PostgreSQL         " -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Pour connecter PgAdmin a PostgreSQL:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. Cliquez sur 'Add New Server'" -ForegroundColor White
Write-Host ""
Write-Host "2. Onglet 'General':" -ForegroundColor White
Write-Host "   - Name: " -NoNewline -ForegroundColor Gray
Write-Host "ShopTracker Production" -ForegroundColor Green
Write-Host ""
Write-Host "3. Onglet 'Connection':" -ForegroundColor White
Write-Host "   - Host:     " -NoNewline -ForegroundColor Gray
Write-Host "postgres" -ForegroundColor Green
Write-Host "   - Port:     " -NoNewline -ForegroundColor Gray
Write-Host "5432" -ForegroundColor Green
Write-Host "   - Database: " -NoNewline -ForegroundColor Gray
Write-Host "shoptracker" -ForegroundColor Green
Write-Host "   - Username: " -NoNewline -ForegroundColor Gray
Write-Host "shoptracker_admin" -ForegroundColor Green
Write-Host "   - Password: " -NoNewline -ForegroundColor Gray
Write-Host "ShopTracker2025!Secure" -ForegroundColor Green
Write-Host ""
Write-Host "4. Cochez 'Save password' et cliquez sur 'Save'" -ForegroundColor White
Write-Host ""

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   Actions Disponibles               " -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Pour arreter PgAdmin:" -ForegroundColor Yellow
Write-Host "  .\stop-pgadmin.ps1" -ForegroundColor White
Write-Host ""
Write-Host "Pour voir les logs PgAdmin:" -ForegroundColor Yellow
Write-Host "  docker logs shoptracker-pgadmin -f" -ForegroundColor White
Write-Host ""

# Attendre que PgAdmin soit prêt
Write-Host "[*] Attente du demarrage de PgAdmin (5 secondes)..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

# Ouvrir le navigateur
Write-Host "[*] Ouverture du navigateur..." -ForegroundColor Yellow
try {
    Start-Process "http://localhost:5050"
    Write-Host "[OK] Navigateur ouvert!" -ForegroundColor Green
} catch {
    Write-Host "[AVERTISSEMENT] Impossible d'ouvrir automatiquement le navigateur." -ForegroundColor Yellow
    Write-Host "Veuillez ouvrir manuellement: http://localhost:5050" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   PgAdmin Pret!                     " -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

