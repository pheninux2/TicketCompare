# ===================================
# ShopTracker - Transfert vers VPS
# ===================================

# Configuration VPS
$VPS_IP = "178.128.162.253"
$VPS_PORT = "443"
$VPS_USER = "root"
$LOCAL_DEPLOY_DIR = "C:\Users\pheni\IdeaProjects\TicketCompare\deploy"
$VPS_SCRIPTS_DIR = "/opt/shoptracker/scripts"

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "   Transfert des Scripts vers VPS       " -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "VPS: ${VPS_USER}@${VPS_IP}:${VPS_PORT}" -ForegroundColor Yellow
Write-Host "Dossier local: $LOCAL_DEPLOY_DIR" -ForegroundColor Yellow
Write-Host "Dossier VPS: $VPS_SCRIPTS_DIR" -ForegroundColor Yellow
Write-Host ""

# Verifier que le dossier local existe
if (-not (Test-Path $LOCAL_DEPLOY_DIR)) {
    Write-Host "[ERREUR] Le dossier $LOCAL_DEPLOY_DIR n'existe pas!" -ForegroundColor Red
    exit 1
}

# Liste des fichiers a transferer
$files = @(
    "setup-vps.sh",
    "deploy-app.sh",
    "update-app.sh",
    "backup.sh",
    "restore.sh",
    "monitor.sh"
)

Write-Host "Fichiers a transferer:" -ForegroundColor Cyan
foreach ($file in $files) {
    $filePath = Join-Path $LOCAL_DEPLOY_DIR $file
    if (Test-Path $filePath) {
        Write-Host "  [OK] $file" -ForegroundColor Green
    } else {
        Write-Host "  [ATTENTION] $file manquant" -ForegroundColor Yellow
    }
}

Write-Host ""
$confirm = Read-Host "Continuer le transfert? (O/n)"
if ($confirm -eq "n" -or $confirm -eq "N") {
    Write-Host "[ERREUR] Transfert annule" -ForegroundColor Red
    exit 0
}

Write-Host ""
Write-Host "[*] Creation du dossier sur le VPS..." -ForegroundColor Cyan

# Creer le dossier sur le VPS si necessaire
$createDirCommand = "mkdir -p $VPS_SCRIPTS_DIR"
ssh -p $VPS_PORT "${VPS_USER}@${VPS_IP}" $createDirCommand

if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Dossier $VPS_SCRIPTS_DIR cree/verifie sur le VPS" -ForegroundColor Green
} else {
    Write-Host "[ERREUR] Impossible de creer le dossier sur le VPS" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[*] Transfert en cours..." -ForegroundColor Cyan
Write-Host ""

# Transferer chaque fichier
$successCount = 0
$failCount = 0

foreach ($file in $files) {
    $filePath = Join-Path $LOCAL_DEPLOY_DIR $file

    if (Test-Path $filePath) {
        Write-Host "[*] Transfert de $file..." -ForegroundColor Cyan

        # Utiliser SCP pour transferer
        scp -P $VPS_PORT "$filePath" "${VPS_USER}@${VPS_IP}:${VPS_SCRIPTS_DIR}/"

        if ($LASTEXITCODE -eq 0) {
            Write-Host "[OK] $file transfere" -ForegroundColor Green
            $successCount++
        } else {
            Write-Host "[ERREUR] Echec du transfert de $file" -ForegroundColor Red
            $failCount++
        }
    } else {
        Write-Host "[ATTENTION] $file non trouve, ignore" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "   Resume du Transfert                 " -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Reussis: $successCount" -ForegroundColor Green
Write-Host "Echoues: $failCount" -ForegroundColor $(if ($failCount -eq 0) { "Green" } else { "Red" })
Write-Host ""

if ($successCount -gt 0) {
    Write-Host "[*] Rendre les scripts executables sur le VPS..." -ForegroundColor Yellow

    # Se connecter au VPS et rendre les scripts executables
    $sshCommand = "cd $VPS_SCRIPTS_DIR ; chmod +x *.sh ; ls -la *.sh"
    ssh -p $VPS_PORT "${VPS_USER}@${VPS_IP}" $sshCommand

    Write-Host ""
    Write-Host "[OK] Scripts prets sur le VPS!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Pour executer installation:" -ForegroundColor Yellow
    Write-Host "  ssh -p $VPS_PORT ${VPS_USER}@${VPS_IP}" -ForegroundColor White
    Write-Host "  cd $VPS_SCRIPTS_DIR" -ForegroundColor White
    Write-Host "  ./setup-vps.sh" -ForegroundColor White
    Write-Host ""
}

