# ===================================
# Retransfert docker-compose.prod.yml
# ===================================

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "   Retransfert docker-compose corrige    " -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

$VPS_IP = "178.128.162.253"
$VPS_PORT = "443"
$VPS_USER = "root"
$LOCAL_FILE = "C:\Users\pheni\IdeaProjects\TicketCompare\deploy\docker-compose.prod.yml"
$VPS_SCRIPTS_DIR = "/opt/shoptracker/scripts"

Write-Host "[*] Transfert de docker-compose.prod.yml corrige..." -ForegroundColor Cyan
scp -P $VPS_PORT "$LOCAL_FILE" "${VPS_USER}@${VPS_IP}:${VPS_SCRIPTS_DIR}/"

if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] docker-compose.prod.yml transfere" -ForegroundColor Green
    Write-Host ""
    Write-Host "[*] Copie vers le dossier app..." -ForegroundColor Yellow

    $sshCommand = "cp $VPS_SCRIPTS_DIR/docker-compose.prod.yml /opt/shoptracker/app/deploy/ ; chown -R deployer:deployer /opt/shoptracker/app"
    ssh -p $VPS_PORT "${VPS_USER}@${VPS_IP}" $sshCommand

    Write-Host "[OK] Fichier corrige et pret sur le VPS!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Vous pouvez maintenant relancer:" -ForegroundColor Yellow
    Write-Host "  cd /opt/shoptracker/scripts && ./deploy-app.sh" -ForegroundColor White
    Write-Host ""
} else {
    Write-Host "[ERREUR] Echec du transfert" -ForegroundColor Red
}

