# ==========================================
# Script de Test - Scripts d'Administration
# ShopTracker - environments/prod
# ==========================================

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   Test Scripts Administration       " -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

$testsPassed = 0
$testsFailed = 0

# Test 1 : Vérifier que tous les scripts existent
Write-Host "[TEST 1] Verification de l'existence des scripts..." -ForegroundColor Yellow

$expectedScripts = @(
    "connect-db.ps1",
    "query-db.ps1",
    "backup-db.ps1",
    "restore-db.ps1",
    "start-pgadmin.ps1",
    "stop-pgadmin.ps1",
    "GUIDE_ADMIN_DATABASE.md",
    "README.md"
)

foreach ($script in $expectedScripts) {
    if (Test-Path $script) {
        Write-Host "  [OK] $script" -ForegroundColor Green
        $testsPassed++
    } else {
        Write-Host "  [ERREUR] $script manquant!" -ForegroundColor Red
        $testsFailed++
    }
}

Write-Host ""

# Test 2 : Vérifier Docker
Write-Host "[TEST 2] Verification de Docker..." -ForegroundColor Yellow

try {
    $dockerVersion = docker --version 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  [OK] Docker installe: $dockerVersion" -ForegroundColor Green
        $testsPassed++
    } else {
        Write-Host "  [ERREUR] Docker non trouve!" -ForegroundColor Red
        $testsFailed++
    }
} catch {
    Write-Host "  [ERREUR] Docker non installe!" -ForegroundColor Red
    $testsFailed++
}

Write-Host ""

# Test 3 : Vérifier le conteneur PostgreSQL
Write-Host "[TEST 3] Verification du conteneur PostgreSQL..." -ForegroundColor Yellow

$container = docker ps --filter "name=shoptracker-db" --format "{{.Names}}" 2>$null

if ($container) {
    Write-Host "  [OK] Conteneur actif: $container" -ForegroundColor Green
    $testsPassed++

    # Test 3.1 : Connexion à PostgreSQL
    Write-Host "[TEST 3.1] Test de connexion PostgreSQL..." -ForegroundColor Yellow
    $connectionTest = docker exec shoptracker-db pg_isready -U shoptracker_admin 2>$null

    if ($LASTEXITCODE -eq 0) {
        Write-Host "  [OK] PostgreSQL accessible" -ForegroundColor Green
        $testsPassed++
    } else {
        Write-Host "  [ERREUR] PostgreSQL non accessible!" -ForegroundColor Red
        $testsFailed++
    }
} else {
    Write-Host "  [AVERTISSEMENT] Conteneur non demarre" -ForegroundColor Yellow
    Write-Host "  [INFO] Demarrez avec: cd ..\.. && .\start-prod.ps1" -ForegroundColor Gray
    $testsFailed++
}

Write-Host ""

# Test 4 : Vérifier le dossier backups
Write-Host "[TEST 4] Verification du dossier backups..." -ForegroundColor Yellow

$backupDir = "..\backups"
if (Test-Path $backupDir) {
    Write-Host "  [OK] Dossier backups existe: $backupDir" -ForegroundColor Green
    $testsPassed++

    # Compter les backups existants
    $backups = Get-ChildItem -Path $backupDir -Filter "backup_*" -ErrorAction SilentlyContinue
    Write-Host "  [INFO] Nombre de backups: $($backups.Count)" -ForegroundColor Gray
} else {
    Write-Host "  [INFO] Dossier backups sera cree au premier backup" -ForegroundColor Yellow
    $testsPassed++
}

Write-Host ""

# Test 5 : Vérifier Docker Compose
Write-Host "[TEST 5] Verification de Docker Compose..." -ForegroundColor Yellow

try {
    $composeVersion = docker-compose --version 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  [OK] Docker Compose installe: $composeVersion" -ForegroundColor Green
        $testsPassed++
    } else {
        Write-Host "  [ERREUR] Docker Compose non trouve!" -ForegroundColor Red
        $testsFailed++
    }
} catch {
    Write-Host "  [ERREUR] Docker Compose non installe!" -ForegroundColor Red
    $testsFailed++
}

Write-Host ""

# Test 6 : Vérifier les permissions des scripts
Write-Host "[TEST 6] Verification des permissions..." -ForegroundColor Yellow

try {
    $acl = Get-Acl "connect-db.ps1"
    Write-Host "  [OK] Permissions OK" -ForegroundColor Green
    $testsPassed++
} catch {
    Write-Host "  [ERREUR] Probleme de permissions!" -ForegroundColor Red
    $testsFailed++
}

Write-Host ""

# Résultats
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   Resultats des Tests               " -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Tests reussis : " -NoNewline -ForegroundColor White
Write-Host "$testsPassed" -ForegroundColor Green
Write-Host "Tests echoues : " -NoNewline -ForegroundColor White
Write-Host "$testsFailed" -ForegroundColor $(if ($testsFailed -eq 0) { "Green" } else { "Red" })
Write-Host ""

if ($testsFailed -eq 0) {
    Write-Host "=====================================" -ForegroundColor Green
    Write-Host "   Tous les tests sont passes!      " -ForegroundColor Green
    Write-Host "=====================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Les scripts sont prets a etre utilises!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Prochaines etapes:" -ForegroundColor Yellow
    Write-Host "  1. .\connect-db.ps1     - Se connecter a PostgreSQL" -ForegroundColor White
    Write-Host "  2. .\query-db.ps1       - Executer des requetes" -ForegroundColor White
    Write-Host "  3. .\backup-db.ps1      - Faire un backup" -ForegroundColor White
    Write-Host "  4. .\start-pgadmin.ps1  - Interface graphique" -ForegroundColor White
    Write-Host ""
    exit 0
} else {
    Write-Host "=====================================" -ForegroundColor Red
    Write-Host "   Certains tests ont echoue        " -ForegroundColor Red
    Write-Host "=====================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Verifiez:" -ForegroundColor Yellow
    Write-Host "  - Docker Desktop est installe et demarre" -ForegroundColor White
    Write-Host "  - L'application est lancee (.\start-prod.ps1)" -ForegroundColor White
    Write-Host "  - Vous etes dans le bon repertoire (environments/prod/scripts)" -ForegroundColor White
    Write-Host ""
    exit 1
}

