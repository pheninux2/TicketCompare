# ===================================
# Retransferer deploy-app.sh corrige
# ===================================

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "   Retransfert du script corrige         " -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

$VPS_IP = "178.128.162.253"
$VPS_PORT = "443"
$VPS_USER = "root"
$LOCAL_FILE = "C:\Users\pheni\IdeaProjects\TicketCompare\deploy\deploy-app.sh"
$VPS_SCRIPTS_DIR = "/opt/shoptracker/scripts"

Write-Host "[*] Transfert de deploy-app.sh corrige..." -ForegroundColor Cyan
scp -P $VPS_PORT "$LOCAL_FILE" "${VPS_USER}@${VPS_IP}:${VPS_SCRIPTS_DIR}/"

if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] deploy-app.sh transfere" -ForegroundColor Green
    Write-Host ""
    Write-Host "[*] Rendre le fichier executable sur le VPS..." -ForegroundColor Yellow

    $sshCommand = "cd $VPS_SCRIPTS_DIR ; chmod +x deploy-app.sh ; sed -i 's/\r$//' deploy-app.sh"
    ssh -p $VPS_PORT "${VPS_USER}@${VPS_IP}" $sshCommand

    Write-Host "[OK] Fichier corrige et pret sur le VPS!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Vous pouvez maintenant relancer:" -ForegroundColor Yellow
    Write-Host "  su - deployer" -ForegroundColor White
    Write-Host "  cd /opt/shoptracker/scripts && ./deploy-app.sh" -ForegroundColor White
    Write-Host ""
} else {
    Write-Host "[ERREUR] Echec du transfert" -ForegroundColor Red
}

