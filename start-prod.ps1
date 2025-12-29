# ===================================
# ShopTracker - Demarrage PRODUCTION
# ===================================

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   ShopTracker - Mode Production" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Verifier Docker
Write-Host "[*] Verification de Docker..." -ForegroundColor Yellow
try {
    docker --version | Out-Null
    Write-Host "[OK] Docker trouve" -ForegroundColor Green
} catch {
    Write-Host "[ERREUR] Docker non installe ou non demarre !" -ForegroundColor Red
    exit 1
}

# Naviguer vers le dossier prod
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location "$scriptPath\environments\prod"

# Verifier le fichier .env
if (-Not (Test-Path ".env")) {
    Write-Host "[ATTENTION] Fichier .env non trouve, creation depuis .env.example..." -ForegroundColor Yellow
    Copy-Item ".env.example" ".env"
    Write-Host "[OK] Fichier .env cree" -ForegroundColor Green
    Write-Host ""
    Write-Host "[ATTENTION] IMPORTANT: Modifiez le fichier environments/prod/.env !" -ForegroundColor Yellow
    Write-Host "   - Changez les mots de passe" -ForegroundColor Yellow
    Write-Host ""

    $response = Read-Host "Continuer avec la config par defaut ? (O/N)"
    if ($response -ne "O" -and $response -ne "o") {
        Write-Host "[ERREUR] Deploiement annule" -ForegroundColor Red
        exit 0
    }
}

Write-Host ""
Write-Host "[*] Arret des conteneurs existants..." -ForegroundColor Yellow
docker-compose down

Write-Host ""
Write-Host "[*] Construction de l'image de production..." -ForegroundColor Yellow
docker-compose build

Write-Host ""
Write-Host "[*] Demarrage en mode production..." -ForegroundColor Yellow
docker-compose up -d

Write-Host ""
Write-Host "[*] Attente du demarrage (30 secondes)..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

Write-Host ""
Write-Host "=====================================" -ForegroundColor Green
Write-Host "   [OK] Mode Production demarre !" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green
Write-Host ""

Write-Host "Application: http://localhost:8080" -ForegroundColor Cyan
Write-Host ""

Write-Host "PgAdmin (Admin uniquement):" -ForegroundColor Cyan
Write-Host "   docker-compose --profile admin up -d" -ForegroundColor Yellow
Write-Host "   http://localhost:5050" -ForegroundColor White
Write-Host ""

Write-Host "Donnees sauvegardees:" -ForegroundColor Cyan
Write-Host "   - BDD: environments/prod/data/postgres" -ForegroundColor White
Write-Host "   - Uploads: environments/prod/data/uploads" -ForegroundColor White
Write-Host "   - Logs: environments/prod/data/logs" -ForegroundColor White
Write-Host "   - Backups: environments/prod/backups" -ForegroundColor White
Write-Host ""

Write-Host "Commandes utiles:" -ForegroundColor Cyan
Write-Host "   - Logs: docker-compose logs -f" -ForegroundColor White
Write-Host "   - Arreter: docker-compose down" -ForegroundColor White
Write-Host "   - Backup: ..\..\backup-db.ps1" -ForegroundColor White
Write-Host ""

Write-Host "Conteneurs actifs:" -ForegroundColor Cyan
docker ps --filter "name=shoptracker" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

Write-Host ""
Write-Host "[OK] Production demarree !" -ForegroundColor Green
Write-Host ""

