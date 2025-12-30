# ==========================================
# Script d'arret PgAdmin - Production
# ShopTracker - environments/prod
# ==========================================

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   Arret de PgAdmin - PRODUCTION    " -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Vérifier si PgAdmin est démarré
$pgadminRunning = docker ps --filter "name=shoptracker-pgadmin" --format "{{.Names}}"

if (-not $pgadminRunning) {
    Write-Host "[INFO] PgAdmin n'est pas demarre." -ForegroundColor Yellow
    Write-Host ""
    exit 0
}

Write-Host "[*] Arret de PgAdmin en cours..." -ForegroundColor Yellow

# Aller dans le répertoire prod
$currentDir = Get-Location
Set-Location -Path ".."

# Arrêter PgAdmin
docker-compose --profile admin stop pgadmin

if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] PgAdmin arrete avec succes!" -ForegroundColor Green
} else {
    Write-Host "[ERREUR] Echec de l'arret de PgAdmin!" -ForegroundColor Red
    Set-Location -Path $currentDir
    exit 1
}

Write-Host ""

# Option pour supprimer complètement le conteneur
Write-Host "Voulez-vous supprimer completement le conteneur PgAdmin? (o/N)" -ForegroundColor Yellow
$response = Read-Host "Reponse"

if ($response -eq "o" -or $response -eq "O" -or $response -eq "oui" -or $response -eq "OUI") {
    Write-Host ""
    Write-Host "[*] Suppression du conteneur PgAdmin..." -ForegroundColor Yellow
    docker-compose --profile admin rm -f pgadmin
    Write-Host "[OK] Conteneur PgAdmin supprime!" -ForegroundColor Green
}

# Retour au répertoire scripts
Set-Location -Path "scripts"

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   PgAdmin Arrete                    " -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Pour redemarrer PgAdmin:" -ForegroundColor Yellow
Write-Host "  .\start-pgadmin.ps1" -ForegroundColor White
Write-Host ""

