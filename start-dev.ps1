# ===================================
# ShopTracker - Demarrage DEVELOPPEMENT
# ===================================

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   ShopTracker - Mode Developpement" -ForegroundColor Cyan
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

# Naviguer vers le dossier dev
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location "$scriptPath\environments\dev"

Write-Host ""
Write-Host "[*] Arret des conteneurs existants..." -ForegroundColor Yellow
docker-compose down

Write-Host ""
Write-Host "[*] Construction de l'image de developpement..." -ForegroundColor Yellow
docker-compose build

Write-Host ""
Write-Host "[*] Demarrage en mode developpement..." -ForegroundColor Yellow
docker-compose up

Write-Host ""
Write-Host "[*] Attente du demarrage (30 secondes)..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

Write-Host ""
Write-Host "=====================================" -ForegroundColor Green
Write-Host "   [OK] Mode Developpement demarre !" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green
Write-Host ""

Write-Host "Application: http://localhost:8080" -ForegroundColor Cyan
Write-Host "H2 Console: http://localhost:8080/h2-console" -ForegroundColor Cyan
Write-Host "    JDBC URL: jdbc:h2:mem:shoptracker" -ForegroundColor White
Write-Host "    User: sa" -ForegroundColor White
Write-Host "    Password: (vide)" -ForegroundColor White
Write-Host ""
Write-Host "Debug Port: localhost:5005" -ForegroundColor Cyan
Write-Host ""

Write-Host "Commandes utiles:" -ForegroundColor Cyan
Write-Host "   - Logs: docker-compose logs -f" -ForegroundColor White
Write-Host "   - Arreter: docker-compose down" -ForegroundColor White
Write-Host "   - Redemarrer: docker-compose restart" -ForegroundColor White
Write-Host ""

Write-Host "Conteneur actif:" -ForegroundColor Cyan
docker ps --filter "name=shoptracker-dev" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

Write-Host ""
Write-Host "[OK] Pret a developper !" -ForegroundColor Green
Write-Host ""

