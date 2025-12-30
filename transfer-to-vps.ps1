# ==========================================
# Script de Transfert vers VPS
# Windows PowerShell
# ==========================================

$VPS_IP = "178.128.162.253"
$VPS_PORT = "8443"
$VPS_USER = "root"
$LOCAL_DEPLOY_DIR = "C:\Users\MHA25660\IdeaProjects\TicketCompare\deploy"
$VPS_SCRIPTS_DIR = "/opt/shoptracker/scripts"

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "   Transfert des Scripts vers VPS      " -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "VPS: $VPS_USER@$VPS_IP:$VPS_PORT" -ForegroundColor Yellow
Write-Host "Dossier local: $LOCAL_DEPLOY_DIR" -ForegroundColor Yellow
Write-Host "Dossier VPS: $VPS_SCRIPTS_DIR" -ForegroundColor Yellow
Write-Host ""

# Vérifier que le dossier local existe
if (-not (Test-Path $LOCAL_DEPLOY_DIR)) {
    Write-Host "[ERREUR] Le dossier $LOCAL_DEPLOY_DIR n'existe pas!" -ForegroundColor Red
    exit 1
}

# Liste des fichiers à transférer
$files = @(
    "setup-vps.sh",
    "deploy-app.sh",
    "update-app.sh",
    "backup.sh",
    "restore.sh",
    "monitor.sh",
    "docker-compose.prod.yml",
    ".env.production.template"
)

Write-Host "Fichiers à transférer:" -ForegroundColor Cyan
foreach ($file in $files) {
    $filePath = Join-Path $LOCAL_DEPLOY_DIR $file
    if (Test-Path $filePath) {
        Write-Host "  ✓ $file" -ForegroundColor Green
    } else {
        Write-Host "  ✗ $file (manquant)" -ForegroundColor Red
    }
}
Write-Host ""

# Confirmation
$response = Read-Host "Continuer le transfert? (O/n)"
if ($response -eq "n" -or $response -eq "N") {
    Write-Host "[INFO] Transfert annulé" -ForegroundColor Yellow
    exit 0
}

Write-Host ""
Write-Host "[*] Transfert en cours..." -ForegroundColor Yellow
Write-Host ""

# Transférer chaque fichier
$successCount = 0
$failCount = 0

foreach ($file in $files) {
    $filePath = Join-Path $LOCAL_DEPLOY_DIR $file

    if (Test-Path $filePath) {
        Write-Host "[*] Transfert de $file..." -ForegroundColor Cyan

        # Utiliser SCP pour transférer
        scp -P $VPS_PORT "$filePath" "${VPS_USER}@${VPS_IP}:${VPS_SCRIPTS_DIR}/"

        if ($LASTEXITCODE -eq 0) {
            Write-Host "[OK] $file transféré" -ForegroundColor Green
            $successCount++
        } else {
            Write-Host "[ERREUR] Échec du transfert de $file" -ForegroundColor Red
            $failCount++
        }
    }
}

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "   Résumé du Transfert                 " -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Réussis: $successCount" -ForegroundColor Green
Write-Host "Échoués: $failCount" -ForegroundColor $(if ($failCount -eq 0) { "Green" } else { "Red" })
Write-Host ""

if ($successCount -gt 0) {
    Write-Host "[*] Rendre les scripts exécutables sur le VPS..." -ForegroundColor Yellow

    # Se connecter au VPS et rendre les scripts exécutables
    $sshCommand = "cd $VPS_SCRIPTS_DIR && chmod +x *.sh && ls -la *.sh"
    ssh -p $VPS_PORT "${VPS_USER}@${VPS_IP}" $sshCommand

    Write-Host ""
    Write-Host "[OK] Scripts prêts sur le VPS!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Pour exécuter l'installation:" -ForegroundColor Yellow
    Write-Host "  ssh -p $VPS_PORT ${VPS_USER}@${VPS_IP}" -ForegroundColor White
    Write-Host "  cd $VPS_SCRIPTS_DIR" -ForegroundColor White
    Write-Host "  ./setup-vps.sh" -ForegroundColor White
    Write-Host ""
}

