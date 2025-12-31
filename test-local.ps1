# ===================================
# Script de Test Local
# Vérifie que l'application fonctionne
# ===================================

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "   Test Local de l'Application          " -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# Vérifier Maven
Write-Host "[*] Vérification de Maven..." -ForegroundColor Yellow
if (Get-Command mvn -ErrorAction SilentlyContinue) {
    Write-Host "✅ Maven installé" -ForegroundColor Green
    mvn -version | Select-String "Apache Maven"
} else {
    Write-Host "❌ Maven non installé" -ForegroundColor Red
    Write-Host "Téléchargez Maven depuis : https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# Vérifier Java
Write-Host "[*] Vérification de Java..." -ForegroundColor Yellow
if (Get-Command java -ErrorAction SilentlyContinue) {
    Write-Host "✅ Java installé" -ForegroundColor Green
    java -version 2>&1 | Select-String "version"
} else {
    Write-Host "❌ Java non installé" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "   Build de l'Application                " -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

$projectRoot = "C:\Users\pheni\IdeaProjects\TicketCompare"
Set-Location $projectRoot

Write-Host "[*] Clean et package Maven..." -ForegroundColor Yellow
mvn clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "❌ Le build a échoué" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "✅ Build réussi !" -ForegroundColor Green

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "   Démarrage de l'Application            " -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "[*] Démarrage de l'application..." -ForegroundColor Yellow
Write-Host "[INFO] L'application va démarrer avec H2 en mémoire" -ForegroundColor Gray
Write-Host "[INFO] Appuyez sur Ctrl+C pour arrêter" -ForegroundColor Gray
Write-Host ""

Start-Sleep -Seconds 2

# Démarrer l'application
cd $projectRoot
mvn spring-boot:run

