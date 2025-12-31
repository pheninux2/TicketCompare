# ===================================
# Script PowerShell - Push vers GitHub
# ===================================

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "   Push vers GitHub                     " -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# Vérifier que Git est installé
if (!(Get-Command git -ErrorAction SilentlyContinue)) {
    Write-Host "[ERREUR] Git n'est pas installé" -ForegroundColor Red
    exit 1
}

# Aller à la racine du projet
$projectRoot = "C:\Users\pheni\IdeaProjects\TicketCompare"
Set-Location $projectRoot

Write-Host "[*] Vérification du repository..." -ForegroundColor Yellow

# Vérifier les fichiers modifiés
Write-Host ""
Write-Host "Fichiers à commiter :" -ForegroundColor Green
git status --short

Write-Host ""
Read-Host "Appuyez sur Entrée pour continuer ou Ctrl+C pour annuler"

# Ajouter les nouveaux fichiers
Write-Host ""
Write-Host "[*] Ajout des fichiers..." -ForegroundColor Yellow

git add .github/workflows/docker-build.yml
git add deploy/deploy-image.sh
git add deploy/GUIDE_MULTI_APPS.md
git add environments/prod/nginx/multi-app.conf

# Commit
Write-Host ""
Write-Host "[*] Création du commit..." -ForegroundColor Yellow

$commitMessage = "feat: Add multi-app deployment with pre-built Docker images

- Add GitHub Actions workflow to build and push Docker images to GHCR
- Add deploy-image.sh script for deploying pre-built images
- Add multi-app Nginx configuration for path-based routing
- Add comprehensive deployment guide
- Support for multiple apps on same VPS (app1, app2, app3, etc.)"

git commit -m $commitMessage

# Push
Write-Host ""
Write-Host "[*] Push vers GitHub..." -ForegroundColor Yellow

git push origin main

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "=========================================" -ForegroundColor Green
    Write-Host "   Push réussi !                        " -ForegroundColor Green
    Write-Host "=========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Prochaines étapes :" -ForegroundColor Cyan
    Write-Host "1. Allez sur GitHub pour vérifier le workflow" -ForegroundColor White
    Write-Host "   https://github.com/pheninux2/TicketCompare/actions" -ForegroundColor Gray
    Write-Host ""
    Write-Host "2. Une fois l'image construite, connectez-vous au VPS :" -ForegroundColor White
    Write-Host "   ssh root@178.128.162.253" -ForegroundColor Gray
    Write-Host ""
    Write-Host "3. Téléchargez et exécutez le script de déploiement :" -ForegroundColor White
    Write-Host "   mkdir -p /opt/shoptracker/app1" -ForegroundColor Gray
    Write-Host "   cd /opt/shoptracker/app1" -ForegroundColor Gray
    Write-Host "   curl -o deploy-image.sh https://raw.githubusercontent.com/pheninux2/TicketCompare/main/deploy/deploy-image.sh" -ForegroundColor Gray
    Write-Host "   chmod +x deploy-image.sh" -ForegroundColor Gray
    Write-Host "   ./deploy-image.sh" -ForegroundColor Gray
    Write-Host ""
    Write-Host "4. Accédez à votre application :" -ForegroundColor White
    Write-Host "   http://178.128.162.253/app1/" -ForegroundColor Gray
    Write-Host ""
} else {
    Write-Host ""
    Write-Host "=========================================" -ForegroundColor Red
    Write-Host "   Erreur lors du push                  " -ForegroundColor Red
    Write-Host "=========================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Vérifiez :" -ForegroundColor Yellow
    Write-Host "1. Que vous êtes bien sur la branche main" -ForegroundColor White
    Write-Host "2. Que vous avez les droits d'accès au repository" -ForegroundColor White
    Write-Host "3. Que vous êtes authentifié avec GitHub" -ForegroundColor White
    Write-Host ""
}

