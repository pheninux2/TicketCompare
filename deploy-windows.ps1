# ===================================
# ShopTracker - Démarrage Production Windows
# ===================================

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   ShopTracker - Déploiement Production" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Vérifier si Docker est installé et en cours d'exécution
Write-Host "🔍 Vérification de Docker..." -ForegroundColor Yellow
try {
    $dockerVersion = docker --version
    Write-Host "✅ Docker trouvé: $dockerVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ ERREUR: Docker n'est pas installé ou n'est pas démarré !" -ForegroundColor Red
    Write-Host "   Installez Docker Desktop pour Windows depuis:" -ForegroundColor Yellow
    Write-Host "   https://www.docker.com/products/docker-desktop" -ForegroundColor Yellow
    exit 1
}

# Vérifier si Docker est en cours d'exécution
try {
    docker ps | Out-Null
    Write-Host "✅ Docker est en cours d'exécution" -ForegroundColor Green
} catch {
    Write-Host "❌ ERREUR: Docker n'est pas démarré !" -ForegroundColor Red
    Write-Host "   Démarrez Docker Desktop" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# Naviguer vers le dossier docker
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptPath\docker

# Vérifier si le fichier .env existe
if (-Not (Test-Path ".env")) {
    Write-Host "⚠️  Fichier .env non trouvé, création depuis .env.prod.example..." -ForegroundColor Yellow
    Copy-Item ".env.prod.example" ".env"
    Write-Host "✅ Fichier .env créé" -ForegroundColor Green
    Write-Host ""
    Write-Host "⚠️  IMPORTANT: Modifiez le fichier docker/.env avec vos configurations !" -ForegroundColor Yellow
    Write-Host "   - Changez les mots de passe" -ForegroundColor Yellow
    Write-Host "   - Configurez l'email si nécessaire" -ForegroundColor Yellow
    Write-Host "   - Ajoutez vos clés Stripe si nécessaire" -ForegroundColor Yellow
    Write-Host ""

    $response = Read-Host "Voulez-vous continuer avec la configuration par défaut ? (O/N)"
    if ($response -ne "O" -and $response -ne "o") {
        Write-Host "❌ Déploiement annulé" -ForegroundColor Red
        exit 0
    }
}

Write-Host ""
Write-Host "🛑 Arrêt des conteneurs existants..." -ForegroundColor Yellow
docker-compose -f docker-compose-prod-windows.yml down

Write-Host ""
Write-Host "🏗️  Construction de l'image Docker (cela peut prendre plusieurs minutes)..." -ForegroundColor Yellow
docker-compose -f docker-compose-prod-windows.yml build

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ ERREUR lors de la construction de l'image !" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🚀 Démarrage des conteneurs en production..." -ForegroundColor Yellow
docker-compose -f docker-compose-prod-windows.yml up -d

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ ERREUR lors du démarrage !" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "⏳ Attente du démarrage de l'application (30 secondes)..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

Write-Host ""
Write-Host "=====================================" -ForegroundColor Green
Write-Host "   ✅ ShopTracker déployé avec succès !" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green
Write-Host ""

Write-Host "📊 Application disponible sur:" -ForegroundColor Cyan
Write-Host "   http://localhost:8080" -ForegroundColor White
Write-Host ""

Write-Host "🔐 Pour accéder à la base de données (ADMIN SEULEMENT):" -ForegroundColor Cyan
Write-Host "   Démarrez PgAdmin avec: " -ForegroundColor White
Write-Host "   docker-compose -f docker-compose-prod-windows.yml --profile admin up -d" -ForegroundColor Yellow
Write-Host "   Puis ouvrez: http://localhost:5050" -ForegroundColor White
Write-Host ""

Write-Host "📁 Données sauvegardées dans:" -ForegroundColor Cyan
Write-Host "   - Base de données: docker/data/postgres" -ForegroundColor White
Write-Host "   - Uploads: docker/data/uploads" -ForegroundColor White
Write-Host "   - Logs: docker/data/logs" -ForegroundColor White
Write-Host "   - Backups: docker/backups" -ForegroundColor White
Write-Host ""

Write-Host "📋 Commandes utiles:" -ForegroundColor Cyan
Write-Host "   - Voir les logs: docker-compose -f docker-compose-prod-windows.yml logs -f" -ForegroundColor White
Write-Host "   - Arrêter: docker-compose -f docker-compose-prod-windows.yml down" -ForegroundColor White
Write-Host "   - Redémarrer: docker-compose -f docker-compose-prod-windows.yml restart" -ForegroundColor White
Write-Host ""

# Afficher les conteneurs en cours d'exécution
Write-Host "📦 Conteneurs actifs:" -ForegroundColor Cyan
docker ps --filter "name=shoptracker" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

Write-Host ""
Write-Host "✅ Déploiement terminé !" -ForegroundColor Green
Write-Host ""

