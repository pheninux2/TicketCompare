# ===================================
# ShopTracker - Backup Base de Donnees
# ===================================

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   ShopTracker - Backup BDD" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Naviguer vers le dossier prod
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptPath\environments\prod

# Verifier si le conteneur est en cours d'execution
$container = docker ps --filter "name=shoptracker-db" --format "{{.Names}}"
if (-Not $container) {
    Write-Host "[ERREUR] Le conteneur de base de donnees n'est pas en cours d'execution !" -ForegroundColor Red
    exit 1
}

# Creer le dossier backups s'il n'existe pas
if (-Not (Test-Path "backups")) {
    New-Item -ItemType Directory -Path "backups" | Out-Null
}

# Generer le nom du fichier de backup avec timestamp
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$backupFile = "shoptracker_backup_$timestamp.sql"
$backupPath = "backups/$backupFile"

Write-Host "[*] Creation du backup de la base de donnees..." -ForegroundColor Yellow
Write-Host "   Fichier: $backupFile" -ForegroundColor White

# Charger les variables d'environnement
if (Test-Path ".env") {
    Get-Content ".env" | ForEach-Object {
        if ($_ -match '^([^#][^=]+)=(.*)$') {
            $name = $matches[1].Trim()
            $value = $matches[2].Trim()
            [Environment]::SetEnvironmentVariable($name, $value, "Process")
        }
    }
}

$dbPassword = $env:DB_PASSWORD
if (-Not $dbPassword) {
    $dbPassword = "ShopTracker2025!Secure"
}

# Effectuer le backup
docker exec -e PGPASSWORD=$dbPassword shoptracker-db pg_dump -U shoptracker_admin -d shoptracker > $backupPath

if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Backup cree avec succes !" -ForegroundColor Green
    Write-Host ""

    $backupSize = (Get-Item $backupPath).Length / 1MB
    Write-Host "Details du backup:" -ForegroundColor Cyan
    Write-Host "   - Fichier: $backupPath" -ForegroundColor White
    Write-Host "   - Taille: $([math]::Round($backupSize, 2)) MB" -ForegroundColor White
    Write-Host "   - Date: $(Get-Date -Format 'dd/MM/yyyy HH:mm:ss')" -ForegroundColor White
    Write-Host ""

    # Compresser le backup
    Write-Host "[*] Compression du backup..." -ForegroundColor Yellow
    $zipFile = "$backupPath.zip"
    Compress-Archive -Path $backupPath -DestinationPath $zipFile -Force
    Remove-Item $backupPath

    $zipSize = (Get-Item $zipFile).Length / 1MB
    Write-Host "[OK] Backup compresse: $zipFile ($([math]::Round($zipSize, 2)) MB)" -ForegroundColor Green
    Write-Host ""

    # Lister les backups existants
    Write-Host "Backups disponibles:" -ForegroundColor Cyan
    Get-ChildItem "backups/*.zip" | Sort-Object LastWriteTime -Descending | Select-Object -First 10 | ForEach-Object {
        $size = $_.Length / 1MB
        Write-Host "   - $($_.Name) ($([math]::Round($size, 2)) MB) - $($_.LastWriteTime)" -ForegroundColor White
    }

    # Nettoyer les anciens backups (garder les 30 derniers)
    $oldBackups = Get-ChildItem "backups/*.zip" | Sort-Object LastWriteTime -Descending | Select-Object -Skip 30
    if ($oldBackups) {
        Write-Host ""
        Write-Host "[*] Suppression des anciens backups (>30 jours)..." -ForegroundColor Yellow
        $oldBackups | ForEach-Object {
            Remove-Item $_.FullName
            Write-Host "   - Supprime: $($_.Name)" -ForegroundColor Gray
        }
    }

} else {
    Write-Host "[ERREUR] Erreur lors de la creation du backup !" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[OK] Backup termine !" -ForegroundColor Green
Write-Host ""

