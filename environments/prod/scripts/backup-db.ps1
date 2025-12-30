# ==========================================
# Script de Backup - Production
# ShopTracker - environments/prod
# ==========================================

param(
    [string]$Format = "dump",  # dump ou sql
    [switch]$Compress = $false
)

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   Backup Base de Donnees           " -ForegroundColor Cyan
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

# Créer le dossier backups s'il n'existe pas
$backupDir = "..\backups"
if (-not (Test-Path $backupDir)) {
    New-Item -ItemType Directory -Path $backupDir -Force | Out-Null
    Write-Host "[*] Dossier backups cree: $backupDir" -ForegroundColor Yellow
}

# Générer le nom du fichier avec horodatage
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$hostname = $env:COMPUTERNAME

if ($Format -eq "sql") {
    $filename = "backup_${hostname}_${timestamp}.sql"
} else {
    $filename = "backup_${hostname}_${timestamp}.dump"
}

$backupPath = Join-Path $backupDir $filename

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   Configuration du Backup           " -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Format        : " -NoNewline -ForegroundColor White
Write-Host $Format -ForegroundColor Green
Write-Host "Fichier       : " -NoNewline -ForegroundColor White
Write-Host $filename -ForegroundColor Green
Write-Host "Destination   : " -NoNewline -ForegroundColor White
Write-Host $backupPath -ForegroundColor Green
Write-Host "Compression   : " -NoNewline -ForegroundColor White
if ($Compress) { Write-Host "Oui" -ForegroundColor Green } else { Write-Host "Non" -ForegroundColor Yellow }
Write-Host ""

# Confirmation
Write-Host "Continuer avec le backup? (O/n)" -ForegroundColor Yellow
$response = Read-Host "Reponse"

if ($response -eq "n" -or $response -eq "N" -or $response -eq "non" -or $response -eq "NON") {
    Write-Host ""
    Write-Host "[INFO] Backup annule par l'utilisateur." -ForegroundColor Yellow
    Write-Host ""
    exit 0
}

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   Backup en Cours...                " -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Exécution du backup
$startTime = Get-Date

try {
    if ($Format -eq "sql") {
        # Backup au format SQL (lisible)
        Write-Host "[*] Creation du backup SQL..." -ForegroundColor Yellow
        docker exec shoptracker-db pg_dump -U shoptracker_admin -d shoptracker | Out-File -FilePath $backupPath -Encoding UTF8
    } else {
        # Backup au format custom (compressé par défaut)
        Write-Host "[*] Creation du backup au format custom..." -ForegroundColor Yellow
        docker exec shoptracker-db pg_dump -U shoptracker_admin -d shoptracker -F c -b -v -f "/backups/$filename" 2>&1 | Out-Null
    }

    if ($LASTEXITCODE -ne 0) {
        throw "Erreur lors de la creation du backup"
    }

    $endTime = Get-Date
    $duration = ($endTime - $startTime).TotalSeconds

    # Vérifier que le fichier existe
    if (Test-Path $backupPath) {
        $fileSize = (Get-Item $backupPath).Length
        $fileSizeMB = [math]::Round($fileSize / 1MB, 2)

        Write-Host ""
        Write-Host "=====================================" -ForegroundColor Cyan
        Write-Host "   Backup Reussi!                    " -ForegroundColor Green
        Write-Host "=====================================" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "Fichier       : " -NoNewline -ForegroundColor White
        Write-Host $filename -ForegroundColor Green
        Write-Host "Taille        : " -NoNewline -ForegroundColor White
        Write-Host "$fileSizeMB MB" -ForegroundColor Green
        Write-Host "Duree         : " -NoNewline -ForegroundColor White
        Write-Host "$([math]::Round($duration, 2)) secondes" -ForegroundColor Green
        Write-Host "Emplacement   : " -NoNewline -ForegroundColor White
        Write-Host $backupPath -ForegroundColor Green
        Write-Host ""

        # Compression supplémentaire si demandée
        if ($Compress -and $Format -eq "sql") {
            Write-Host "[*] Compression du fichier..." -ForegroundColor Yellow
            $zipPath = "$backupPath.zip"
            Compress-Archive -Path $backupPath -DestinationPath $zipPath -Force

            if (Test-Path $zipPath) {
                $zipSize = (Get-Item $zipPath).Length
                $zipSizeMB = [math]::Round($zipSize / 1MB, 2)
                $compressionRatio = [math]::Round((1 - ($zipSize / $fileSize)) * 100, 2)

                Write-Host "[OK] Fichier compresse: $zipPath" -ForegroundColor Green
                Write-Host "     Taille: $zipSizeMB MB (gain de $compressionRatio%)" -ForegroundColor Green
                Write-Host ""

                # Supprimer le fichier non compressé
                Remove-Item $backupPath -Force
                Write-Host "[*] Fichier original supprime (version compresse conservee)" -ForegroundColor Yellow
            }
        }

        # Lister les backups existants
        Write-Host "=====================================" -ForegroundColor Cyan
        Write-Host "   Backups Existants                 " -ForegroundColor Cyan
        Write-Host "=====================================" -ForegroundColor Cyan
        Write-Host ""

        $backups = Get-ChildItem -Path $backupDir -Filter "backup_*" | Sort-Object LastWriteTime -Descending | Select-Object -First 5
        foreach ($backup in $backups) {
            $size = [math]::Round($backup.Length / 1MB, 2)
            $date = $backup.LastWriteTime.ToString("yyyy-MM-dd HH:mm:ss")
            Write-Host "  $($backup.Name) - $size MB - $date" -ForegroundColor White
        }
        Write-Host ""

        # Nettoyage des anciens backups
        $oldBackups = Get-ChildItem -Path $backupDir -Filter "backup_*" | Where-Object { $_.LastWriteTime -lt (Get-Date).AddDays(-30) }
        if ($oldBackups.Count -gt 0) {
            Write-Host "[*] $($oldBackups.Count) backup(s) de plus de 30 jours trouve(s)" -ForegroundColor Yellow
            Write-Host "Voulez-vous les supprimer? (o/N)" -ForegroundColor Yellow
            $cleanResponse = Read-Host "Reponse"

            if ($cleanResponse -eq "o" -or $cleanResponse -eq "O") {
                foreach ($old in $oldBackups) {
                    Remove-Item $old.FullName -Force
                    Write-Host "  [X] Supprime: $($old.Name)" -ForegroundColor Red
                }
                Write-Host "[OK] Anciens backups supprimes!" -ForegroundColor Green
            }
        }

    } else {
        throw "Le fichier de backup n'a pas ete cree"
    }

} catch {
    Write-Host ""
    Write-Host "=====================================" -ForegroundColor Cyan
    Write-Host "   Erreur lors du Backup            " -ForegroundColor Red
    Write-Host "=====================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "[ERREUR] $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "Verifiez:" -ForegroundColor Yellow
    Write-Host "  - Que le conteneur PostgreSQL est actif" -ForegroundColor White
    Write-Host "  - Que vous avez les droits d'ecriture dans $backupDir" -ForegroundColor White
    Write-Host "  - Les logs: docker logs shoptracker-db --tail 50" -ForegroundColor White
    Write-Host ""
    exit 1
}

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "[OK] Backup termine avec succes!" -ForegroundColor Green
Write-Host ""
Write-Host "Pour restaurer ce backup:" -ForegroundColor Yellow
Write-Host "  .\restore-db.ps1 $filename" -ForegroundColor White
Write-Host ""

