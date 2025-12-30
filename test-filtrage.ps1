# Script de test du filtrage consommation
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  TEST FILTRAGE CONSOMMATION" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "[1] Compilation du projet..." -ForegroundColor Yellow
mvn clean compile -DskipTests -q

if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Compilation réussie" -ForegroundColor Green
} else {
    Write-Host "[ERREUR] Échec de la compilation" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[2] Démarrage de l'application..." -ForegroundColor Yellow
Write-Host "    Appuyez sur Ctrl+C pour arrêter" -ForegroundColor Gray
Write-Host ""
Write-Host "Une fois l'application démarrée :" -ForegroundColor Cyan
Write-Host "  1. Allez sur: http://localhost:8080/consumption/weekly?period=month" -ForegroundColor White
Write-Host "  2. Regardez les LOGS dans cette console" -ForegroundColor White
Write-Host "  3. Cherchez les lignes:" -ForegroundColor White
Write-Host "     - 'Date actuelle (LocalDate.now()): ...'" -ForegroundColor Gray
Write-Host "     - 'Période MOIS calculée - ...'" -ForegroundColor Gray
Write-Host "     - 'Total produits en base: ...'" -ForegroundColor Gray
Write-Host "     - 'Produits après filtrage période: ...'" -ForegroundColor Gray
Write-Host ""

Start-Sleep -Seconds 2

mvn spring-boot:run

