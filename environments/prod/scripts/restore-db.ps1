# ==========================================
# Script de Restauration - Production
# ShopTracker - environments/prod
# ==========================================

param(
    [Parameter(Mandatory=$false)]
    [string]$BackupFile
)

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   Restauration Base de Donnees     " -ForegroundColor Cyan
Write-Host "   Environnement: PRODUCTION        " -ForegroundColor Yellow
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

$backupDir = "..\backups"

# Si aucun fichier spécifié, lister les backups disponibles
if (-not $BackupFile) {
    Write-Host "=====================================" -ForegroundColor Cyan
    Write-Host "   Backups Disponibles               " -ForegroundColor Cyan
    Write-Host "=====================================" -ForegroundColor Cyan
    Write-Host ""

    $backups = Get-ChildItem -Path $backupDir -Filter "backup_*" | Sort-Object LastWriteTime -Descending

    if ($backups.Count -eq 0) {
        Write-Host "[ERREUR] Aucun backup trouve dans $backupDir" -ForegroundColor Red
        Write-Host ""
        Write-Host "Creez d'abord un backup:" -ForegroundColor Yellow
        Write-Host "  .\backup-db.ps1" -ForegroundColor White
        Write-Host ""
        exit 1
    }

    for ($i = 0; $i -lt $backups.Count; $i++) {
        $backup = $backups[$i]
        $size = [math]::Round($backup.Length / 1MB, 2)
        $date = $backup.LastWriteTime.ToString("yyyy-MM-dd HH:mm:ss")
        Write-Host "  $($i+1). $($backup.Name)" -ForegroundColor White
        Write-Host "     Taille: $size MB - Date: $date" -ForegroundColor Gray
        Write-Host ""
    }

    Write-Host "Selectionnez un backup (1-$($backups.Count)) ou 0 pour annuler:" -ForegroundColor Yellow
    $choice = Read-Host "Choix"

    if ($choice -eq "0" -or $choice -eq "") {
        Write-Host ""
        Write-Host "[INFO] Restauration annulee." -ForegroundColor Yellow
        Write-Host ""
        exit 0
    }

    $index = [int]$choice - 1
    if ($index -lt 0 -or $index -ge $backups.Count) {
        Write-Host ""
        Write-Host "[ERREUR] Choix invalide!" -ForegroundColor Red
        Write-Host ""
        exit 1
    }

    $BackupFile = $backups[$index].Name
}

$backupPath = Join-Path $backupDir $BackupFile

# Vérifier que le fichier existe
if (-not (Test-Path $backupPath)) {
    Write-Host "[ERREUR] Fichier de backup introuvable: $backupPath" -ForegroundColor Red
    Write-Host ""
    exit 1
}

$fileSize = (Get-Item $backupPath).Length
$fileSizeMB = [math]::Round($fileSize / 1MB, 2)

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   Configuration de la Restauration " -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Fichier       : " -NoNewline -ForegroundColor White
Write-Host $BackupFile -ForegroundColor Green
Write-Host "Taille        : " -NoNewline -ForegroundColor White
Write-Host "$fileSizeMB MB" -ForegroundColor Green
Write-Host "Base de donnees: " -NoNewline -ForegroundColor White
Write-Host "shoptracker" -ForegroundColor Green
Write-Host ""

# AVERTISSEMENT
Write-Host "=====================================" -ForegroundColor Red
Write-Host "   AVERTISSEMENT                     " -ForegroundColor Red
Write-Host "=====================================" -ForegroundColor Red
Write-Host ""
Write-Host "Cette operation va:" -ForegroundColor Yellow
Write-Host "  - SUPPRIMER toutes les donnees actuelles" -ForegroundColor Red
Write-Host "  - Restaurer les donnees du backup" -ForegroundColor Yellow
Write-Host "  - Cette action est IRREVERSIBLE" -ForegroundColor Red
Write-Host ""
Write-Host "Voulez-vous creer un backup de securite avant? (O/n)" -ForegroundColor Yellow
$backupResponse = Read-Host "Reponse"

if ($backupResponse -ne "n" -and $backupResponse -ne "N" -and $backupResponse -ne "non") {
    Write-Host ""
    Write-Host "[*] Creation d'un backup de securite..." -ForegroundColor Yellow
    $safetyBackup = "backup_safety_$(Get-Date -Format 'yyyyMMdd_HHmmss').dump"
    docker exec shoptracker-db pg_dump -U shoptracker_admin -d shoptracker -F c -b -f "/backups/$safetyBackup" 2>&1 | Out-Null

    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] Backup de securite cree: $safetyBackup" -ForegroundColor Green
    } else {
        Write-Host "[AVERTISSEMENT] Impossible de creer le backup de securite!" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "Tapez 'RESTAURER' pour confirmer la restauration:" -ForegroundColor Yellow
$confirmation = Read-Host "Confirmation"

if ($confirmation -ne "RESTAURER") {
    Write-Host ""
    Write-Host "[INFO] Restauration annulee." -ForegroundColor Yellow
    Write-Host ""
    exit 0
}

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   Restauration en Cours...          " -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

$startTime = Get-Date

try {
    # Détecter le format du fichier
    if ($BackupFile -like "*.sql" -or $BackupFile -like "*.sql.zip") {
        # Format SQL
        Write-Host "[*] Restauration depuis un fichier SQL..." -ForegroundColor Yellow

        # Décompresser si nécessaire
        if ($BackupFile -like "*.zip") {
            Write-Host "[*] Decompression du fichier..." -ForegroundColor Yellow
            $tempSql = $BackupFile -replace ".zip", ""
            Expand-Archive -Path $backupPath -DestinationPath $backupDir -Force
            $backupPath = Join-Path $backupDir $tempSql
        }

        Get-Content $backupPath | docker exec -i shoptracker-db psql -U shoptracker_admin -d shoptracker

    } else {
        # Format custom (dump)
        Write-Host "[*] Restauration depuis un fichier dump..." -ForegroundColor Yellow
        docker exec shoptracker-db pg_restore -U shoptracker_admin -d shoptracker -v -c /backups/$BackupFile 2>&1 | Out-Null
    }

    if ($LASTEXITCODE -ne 0) {
        throw "Erreur lors de la restauration"
    }

    $endTime = Get-Date
    $duration = ($endTime - $startTime).TotalSeconds

    Write-Host ""
    Write-Host "=====================================" -ForegroundColor Cyan
    Write-Host "   Restauration Reussie!             " -ForegroundColor Green
    Write-Host "=====================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Fichier       : " -NoNewline -ForegroundColor White
    Write-Host $BackupFile -ForegroundColor Green
    Write-Host "Duree         : " -NoNewline -ForegroundColor White
    Write-Host "$([math]::Round($duration, 2)) secondes" -ForegroundColor Green
    Write-Host ""

    # Vérification post-restauration
    Write-Host "[*] Verification de la restauration..." -ForegroundColor Yellow

    $ticketCount = docker exec shoptracker-db psql -U shoptracker_admin -d shoptracker -t -c "SELECT COUNT(*) FROM tickets;" 2>$null
    $storeCount = docker exec shoptracker-db psql -U shoptracker_admin -d shoptracker -t -c "SELECT COUNT(*) FROM stores;" 2>$null
    $userCount = docker exec shoptracker-db psql -U shoptracker_admin -d shoptracker -t -c "SELECT COUNT(*) FROM users;" 2>$null

    Write-Host ""
    Write-Host "Donnees restaurees:" -ForegroundColor Cyan
    Write-Host "  - Tickets : $($ticketCount.Trim())" -ForegroundColor White
    Write-Host "  - Magasins: $($storeCount.Trim())" -ForegroundColor White
    Write-Host "  - Utilisateurs: $($userCount.Trim())" -ForegroundColor White
    Write-Host ""

} catch {
    Write-Host ""
    Write-Host "=====================================" -ForegroundColor Cyan
    Write-Host "   Erreur lors de la Restauration   " -ForegroundColor Red
    Write-Host "=====================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "[ERREUR] $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "Verifiez:" -ForegroundColor Yellow
    Write-Host "  - Que le conteneur PostgreSQL est actif" -ForegroundColor White
    Write-Host "  - Que le fichier de backup est valide" -ForegroundColor White
    Write-Host "  - Les logs: docker logs shoptracker-db --tail 50" -ForegroundColor White
    Write-Host ""

    if (Test-Path "$backupDir/backup_safety_*") {
        Write-Host "Un backup de securite a ete cree avant la restauration." -ForegroundColor Yellow
        Write-Host "Vous pouvez le restaurer si necessaire." -ForegroundColor Yellow
    }

    Write-Host ""
    exit 1
}

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "[OK] Restauration terminee avec succes!" -ForegroundColor Green
Write-Host ""
Write-Host "Recommandations:" -ForegroundColor Yellow
Write-Host "  - Redemarrez l'application pour prendre en compte les changements" -ForegroundColor White
Write-Host "  - Verifiez que les donnees sont correctes" -ForegroundColor White
Write-Host ""

