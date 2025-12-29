# ===================================
# ShopTracker - Restauration Base de Donnees
# ===================================

param(
    [Parameter(Mandatory=$false)]
    [string]$BackupFile
)

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   ShopTracker - Restauration BDD" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Naviguer vers le dossier prod
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptPath\environments\prod

# Verifier si le dossier backups existe
if (-Not (Test-Path "backups")) {
    Write-Host "[ERREUR] Aucun backup trouve !" -ForegroundColor Red
    exit 1
}

# Lister les backups disponibles si aucun fichier n'est specifie
if (-Not $BackupFile) {
    Write-Host "Backups disponibles:" -ForegroundColor Cyan
    $backups = Get-ChildItem "backups/*.zip" | Sort-Object LastWriteTime -Descending

    if ($backups.Count -eq 0) {
        Write-Host "[ERREUR] Aucun backup trouve !" -ForegroundColor Red
        exit 1
    }

    for ($i = 0; $i -lt $backups.Count; $i++) {
        $backup = $backups[$i]
        $size = $backup.Length / 1MB
        Write-Host "   [$($i + 1)] $($backup.Name) ($([math]::Round($size, 2)) MB) - $($backup.LastWriteTime)" -ForegroundColor White
    }

    Write-Host ""
    $selection = Read-Host "Selectionnez un backup (1-$($backups.Count)) ou Q pour quitter"

    if ($selection -eq "Q" -or $selection -eq "q") {
        Write-Host "[ERREUR] Restauration annulee" -ForegroundColor Red
        exit 0
    }

    $index = [int]$selection - 1
    if ($index -lt 0 -or $index -ge $backups.Count) {
        Write-Host "[ERREUR] Selection invalide !" -ForegroundColor Red
        exit 1
    }

    $BackupFile = $backups[$index].FullName
}

# Verifier si le fichier existe
if (-Not (Test-Path $BackupFile)) {
    Write-Host "[ERREUR] Fichier de backup non trouve : $BackupFile" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[ATTENTION] Cette operation va ECRASER toutes les donnees actuelles !" -ForegroundColor Red
Write-Host "   Backup selectionne: $(Split-Path $BackupFile -Leaf)" -ForegroundColor Yellow
Write-Host ""
$confirm = Read-Host "Etes-vous sur de vouloir continuer ? (CONFIRMER pour continuer)"

if ($confirm -ne "CONFIRMER") {
    Write-Host "[ERREUR] Restauration annulee" -ForegroundColor Red
    exit 0
}

# Verifier si le conteneur est en cours d'execution
$container = docker ps --filter "name=shoptracker-db" --format "{{.Names}}"
if (-Not $container) {
    Write-Host "[ERREUR] Le conteneur de base de donnees n'est pas en cours d'execution !" -ForegroundColor Red
    exit 1
}

# Decompresser le backup si c'est un zip
$sqlFile = $BackupFile
if ($BackupFile -like "*.zip") {
    Write-Host "[*] Decompression du backup..." -ForegroundColor Yellow
    $tempFolder = "backups/temp"
    if (Test-Path $tempFolder) {
        Remove-Item $tempFolder -Recurse -Force
    }
    New-Item -ItemType Directory -Path $tempFolder | Out-Null
    Expand-Archive -Path $BackupFile -DestinationPath $tempFolder
    $sqlFile = Get-ChildItem "$tempFolder/*.sql" | Select-Object -First 1 | ForEach-Object { $_.FullName }
}

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

Write-Host ""
Write-Host "[*] Restauration de la base de donnees..." -ForegroundColor Yellow

# Copier le fichier SQL dans le conteneur
docker cp $sqlFile shoptracker-db:/tmp/restore.sql

# Arreter l'application pour eviter les conflits
Write-Host "   Arret temporaire de l'application..." -ForegroundColor Gray
docker-compose stop app

# Supprimer les connexions actives et restaurer
docker exec -e PGPASSWORD=$dbPassword shoptracker-db psql -U shoptracker_admin -d postgres -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'shoptracker' AND pid <> pg_backend_pid();"
docker exec -e PGPASSWORD=$dbPassword shoptracker-db dropdb -U shoptracker_admin shoptracker
docker exec -e PGPASSWORD=$dbPassword shoptracker-db createdb -U shoptracker_admin shoptracker
docker exec -e PGPASSWORD=$dbPassword shoptracker-db psql -U shoptracker_admin -d shoptracker -f /tmp/restore.sql > $null 2>&1

# Nettoyer
docker exec shoptracker-db rm /tmp/restore.sql
if (Test-Path "backups/temp") {
    Remove-Item "backups/temp" -Recurse -Force
}

# Redemarrer l'application
Write-Host "   Redemarrage de l'application..." -ForegroundColor Gray
docker-compose start app

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "[OK] Restauration reussie !" -ForegroundColor Green
    Write-Host ""
    Write-Host "Base de donnees restauree depuis:" -ForegroundColor Cyan
    Write-Host "   $BackupFile" -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "[ERREUR] Erreur lors de la restauration !" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[OK] Restauration terminee !" -ForegroundColor Green
Write-Host ""

