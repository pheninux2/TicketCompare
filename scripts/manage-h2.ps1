#!/usr/bin/env pwsh
# ===================================
# Script de Gestion H2 Persistance
# ===================================

param(
    [Parameter(Position=0)]
    [ValidateSet('start', 'stop', 'restart', 'status', 'backup', 'restore', 'reset', 'logs', 'console')]
    [string]$Action = 'start',

    [Parameter()]
    [ValidateSet('file', 'mem')]
    [string]$Mode = 'file',

    [Parameter()]
    [string]$BackupFile = "h2-backup-$(Get-Date -Format 'yyyyMMdd-HHmmss').tar.gz"
)

$DockerCompose = "docker-compose"
$ComposeFile = "docker/docker-compose-h2.yml"
$VolumeName = "ticketcompare_h2_data"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " TicketCompare - Gestion H2 Persistance" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

function Show-Status {
    Write-Host "📊 Statut de l'application" -ForegroundColor Yellow
    Write-Host ""

    # Vérifier si le conteneur tourne
    $container = docker ps -a --filter "name=ticketcompare-app-h2" --format "{{.Status}}"
    if ($container) {
        Write-Host "Conteneur: " -NoNewline
        if ($container -like "Up*") {
            Write-Host "✅ En cours d'exécution" -ForegroundColor Green
        } else {
            Write-Host "❌ Arrêté" -ForegroundColor Red
        }
        Write-Host "Détails: $container"
    } else {
        Write-Host "❌ Conteneur non trouvé" -ForegroundColor Red
    }

    Write-Host ""

    # Vérifier le volume
    $volume = docker volume ls --filter "name=$VolumeName" --format "{{.Name}}"
    if ($volume) {
        Write-Host "Volume de données: ✅ Existe ($VolumeName)" -ForegroundColor Green
        $volumeInfo = docker volume inspect $VolumeName | ConvertFrom-Json
        Write-Host "Mountpoint: $($volumeInfo.Mountpoint)"
    } else {
        Write-Host "Volume de données: ❌ N'existe pas" -ForegroundColor Red
    }

    Write-Host ""
    Write-Host "URLs:" -ForegroundColor Yellow
    Write-Host "  Application: http://localhost:8080"
    Write-Host "  H2 Console:  http://localhost:8080/h2-console"
}

function Start-App {
    Write-Host "🚀 Démarrage de l'application (Mode: $Mode)" -ForegroundColor Green

    if ($Mode -eq 'file') {
        Write-Host "   Mode PERSISTANT - Les données seront sauvegardées" -ForegroundColor Green
        $env:H2_DB_URL = "jdbc:h2:file:/app/data/ticketcomparedb;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE"
        $env:JPA_DDL_AUTO = "update"
    } else {
        Write-Host "   Mode MÉMOIRE - Les données seront perdues au redémarrage" -ForegroundColor Yellow
        $env:H2_DB_URL = "jdbc:h2:mem:ticketcomparedb;DB_CLOSE_ON_EXIT=FALSE"
        $env:JPA_DDL_AUTO = "create"
    }

    & $DockerCompose -f $ComposeFile up -d

    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "✅ Application démarrée avec succès !" -ForegroundColor Green
        Write-Host ""
        Write-Host "Attendez quelques secondes que l'application démarre complètement..." -ForegroundColor Yellow
        Start-Sleep -Seconds 10

        Write-Host ""
        Write-Host "📊 Accès:" -ForegroundColor Cyan
        Write-Host "  Application: http://localhost:8080" -ForegroundColor White
        Write-Host "  H2 Console:  http://localhost:8080/h2-console" -ForegroundColor White
        Write-Host ""
        Write-Host "  JDBC URL:    jdbc:h2:file:/app/data/ticketcomparedb" -ForegroundColor White
        Write-Host "  Username:    sa" -ForegroundColor White
        Write-Host "  Password:    (vide)" -ForegroundColor White
    } else {
        Write-Host "❌ Erreur lors du démarrage" -ForegroundColor Red
    }
}

function Stop-App {
    Write-Host "⏹️  Arrêt de l'application" -ForegroundColor Yellow
    & $DockerCompose -f $ComposeFile down

    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Application arrêtée" -ForegroundColor Green
    } else {
        Write-Host "❌ Erreur lors de l'arrêt" -ForegroundColor Red
    }
}

function Restart-App {
    Write-Host "🔄 Redémarrage de l'application" -ForegroundColor Yellow
    Stop-App
    Start-Sleep -Seconds 2
    Start-App
}

function Backup-Data {
    Write-Host "💾 Backup de la base de données" -ForegroundColor Cyan
    Write-Host "   Fichier: $BackupFile" -ForegroundColor White

    # Vérifier si le volume existe
    $volume = docker volume ls --filter "name=$VolumeName" --format "{{.Name}}"
    if (-not $volume) {
        Write-Host "❌ Volume $VolumeName non trouvé !" -ForegroundColor Red
        return
    }

    # Créer le backup
    docker run --rm `
        -v "${VolumeName}:/data" `
        -v "${PWD}:/backup" `
        ubuntu tar czf "/backup/$BackupFile" /data

    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Backup créé: $BackupFile" -ForegroundColor Green
        $fileInfo = Get-Item $BackupFile
        Write-Host "   Taille: $([math]::Round($fileInfo.Length / 1MB, 2)) MB"
    } else {
        Write-Host "❌ Erreur lors du backup" -ForegroundColor Red
    }
}

function Restore-Data {
    if (-not (Test-Path $BackupFile)) {
        Write-Host "❌ Fichier de backup non trouvé: $BackupFile" -ForegroundColor Red
        Write-Host "   Spécifiez le fichier avec -BackupFile" -ForegroundColor Yellow
        return
    }

    Write-Host "📥 Restauration de la base de données" -ForegroundColor Cyan
    Write-Host "   Depuis: $BackupFile" -ForegroundColor White
    Write-Host ""
    Write-Host "⚠️  ATTENTION: Cette opération va écraser les données existantes !" -ForegroundColor Yellow
    $confirm = Read-Host "   Continuer ? (oui/non)"

    if ($confirm -ne 'oui') {
        Write-Host "❌ Restauration annulée" -ForegroundColor Red
        return
    }

    # Arrêter l'application
    Write-Host "   Arrêt de l'application..." -ForegroundColor Yellow
    Stop-App
    Start-Sleep -Seconds 2

    # Restaurer le backup
    docker run --rm `
        -v "${VolumeName}:/data" `
        -v "${PWD}:/backup" `
        ubuntu tar xzf "/backup/$BackupFile" -C /

    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Données restaurées avec succès" -ForegroundColor Green
        Write-Host "   Redémarrage de l'application..." -ForegroundColor Yellow
        Start-App
    } else {
        Write-Host "❌ Erreur lors de la restauration" -ForegroundColor Red
    }
}

function Reset-Data {
    Write-Host "🗑️  Reset de la base de données" -ForegroundColor Red
    Write-Host ""
    Write-Host "⚠️  ATTENTION: Cette opération va SUPPRIMER TOUTES les données !" -ForegroundColor Yellow
    Write-Host "   - Tous les tickets" -ForegroundColor Red
    Write-Host "   - Tous les produits" -ForegroundColor Red
    Write-Host "   - Toutes les statistiques" -ForegroundColor Red
    Write-Host ""
    $confirm = Read-Host "   Êtes-vous sûr ? (SUPPRIMER pour confirmer)"

    if ($confirm -ne 'SUPPRIMER') {
        Write-Host "❌ Reset annulé" -ForegroundColor Red
        return
    }

    # Arrêter l'application
    Write-Host "   Arrêt de l'application..." -ForegroundColor Yellow
    Stop-App
    Start-Sleep -Seconds 2

    # Supprimer le volume
    Write-Host "   Suppression du volume..." -ForegroundColor Yellow
    docker volume rm $VolumeName

    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Volume supprimé" -ForegroundColor Green
        Write-Host "   Redémarrage avec base vide..." -ForegroundColor Yellow
        Start-App
    } else {
        Write-Host "❌ Erreur lors de la suppression du volume" -ForegroundColor Red
    }
}

function Show-Logs {
    Write-Host "📄 Logs de l'application" -ForegroundColor Cyan
    Write-Host "   (Ctrl+C pour quitter)" -ForegroundColor Yellow
    Write-Host ""
    & $DockerCompose -f $ComposeFile logs -f
}

function Open-Console {
    Write-Host "🌐 Ouverture de la console H2..." -ForegroundColor Cyan
    Write-Host ""
    Write-Host "   URL: http://localhost:8080/h2-console" -ForegroundColor White
    Write-Host ""
    Write-Host "   Paramètres de connexion:" -ForegroundColor Yellow
    Write-Host "   JDBC URL:  jdbc:h2:file:/app/data/ticketcomparedb" -ForegroundColor White
    Write-Host "   Username:  sa" -ForegroundColor White
    Write-Host "   Password:  (vide)" -ForegroundColor White
    Write-Host ""

    Start-Process "http://localhost:8080/h2-console"
}

# Exécuter l'action
switch ($Action) {
    'start'   { Start-App }
    'stop'    { Stop-App }
    'restart' { Restart-App }
    'status'  { Show-Status }
    'backup'  { Backup-Data }
    'restore' { Restore-Data }
    'reset'   { Reset-Data }
    'logs'    { Show-Logs }
    'console' { Open-Console }
}

Write-Host ""

