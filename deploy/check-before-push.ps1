# ===================================
# Script de Vérification Pre-Push
# ===================================

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "   Vérification Pre-Push                " -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

$projectRoot = "C:\Users\pheni\IdeaProjects\TicketCompare"
Set-Location $projectRoot

$errors = @()
$warnings = @()
$success = @()

# Vérifier les fichiers critiques
Write-Host "[*] Vérification des fichiers critiques..." -ForegroundColor Yellow

$criticalFiles = @(
    ".github\workflows\docker-build.yml",
    "deploy\deploy-image.sh",
    "deploy\update-image.sh",
    "deploy\GUIDE_MULTI_APPS.md",
    "deploy\QUICK_START.md",
    "deploy\SPRING_PROXY_CONFIG.md",
    "deploy\commands.sh",
    "environments\prod\nginx\multi-app.conf",
    "src\main\resources\application.yml",
    "src\main\resources\application-prod.yml"
)

foreach ($file in $criticalFiles) {
    if (Test-Path $file) {
        $success += "✅ $file"
    } else {
        $errors += "❌ Fichier manquant: $file"
    }
}

# Vérifier la configuration Spring Boot
Write-Host "[*] Vérification de la configuration Spring Boot..." -ForegroundColor Yellow

$appYml = Get-Content "src\main\resources\application.yml" -Raw
$appProdYml = Get-Content "src\main\resources\application-prod.yml" -Raw

if ($appYml -match "forward-headers-strategy: framework") {
    $success += "✅ application.yml - forward-headers-strategy configuré"
} else {
    $warnings += "⚠️  application.yml - forward-headers-strategy non configuré"
}

if ($appProdYml -match "forward-headers-strategy: framework") {
    $success += "✅ application-prod.yml - forward-headers-strategy configuré"
} else {
    $warnings += "⚠️  application-prod.yml - forward-headers-strategy non configuré"
}

# Vérifier GitHub Actions workflow
Write-Host "[*] Vérification du workflow GitHub Actions..." -ForegroundColor Yellow

$workflow = Get-Content ".github\workflows\docker-build.yml" -Raw

if ($workflow -match "docker/build-push-action") {
    $success += "✅ Workflow GitHub Actions - build-push configuré"
} else {
    $errors += "❌ Workflow GitHub Actions - build-push non configuré"
}

if ($workflow -match "ghcr.io") {
    $success += "✅ Workflow GitHub Actions - GHCR configuré"
} else {
    $errors += "❌ Workflow GitHub Actions - GHCR non configuré"
}

# Vérifier les scripts de déploiement
Write-Host "[*] Vérification des scripts de déploiement..." -ForegroundColor Yellow

$deployScript = Get-Content "deploy\deploy-image.sh" -Raw

if ($deployScript -match "docker pull") {
    $success += "✅ deploy-image.sh - docker pull présent"
} else {
    $errors += "❌ deploy-image.sh - docker pull manquant"
}

if ($deployScript -match "ghcr.io") {
    $success += "✅ deploy-image.sh - GHCR référencé"
} else {
    $warnings += "⚠️  deploy-image.sh - GHCR non référencé"
}

# Vérifier la configuration Nginx
Write-Host "[*] Vérification de la configuration Nginx..." -ForegroundColor Yellow

$nginxConf = Get-Content "environments\prod\nginx\multi-app.conf" -Raw

if ($nginxConf -match "/app1/") {
    $success += "✅ multi-app.conf - route /app1/ configurée"
} else {
    $errors += "❌ multi-app.conf - route /app1/ manquante"
}

if ($nginxConf -match "proxy_pass") {
    $success += "✅ multi-app.conf - proxy_pass configuré"
} else {
    $errors += "❌ multi-app.conf - proxy_pass manquant"
}

# Vérifier Git
Write-Host "[*] Vérification Git..." -ForegroundColor Yellow

if (Get-Command git -ErrorAction SilentlyContinue) {
    $success += "✅ Git installé"

    $branch = git branch --show-current
    if ($branch -eq "main" -or $branch -eq "master") {
        $success += "✅ Branche: $branch"
    } else {
        $warnings += "⚠️  Branche actuelle: $branch (attendu: main ou master)"
    }
} else {
    $errors += "❌ Git non installé"
}

# Afficher les résultats
Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "   Résultats de la Vérification         " -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

if ($success.Count -gt 0) {
    Write-Host "✅ Succès ($($success.Count)):" -ForegroundColor Green
    foreach ($s in $success) {
        Write-Host "   $s" -ForegroundColor Green
    }
    Write-Host ""
}

if ($warnings.Count -gt 0) {
    Write-Host "⚠️  Avertissements ($($warnings.Count)):" -ForegroundColor Yellow
    foreach ($w in $warnings) {
        Write-Host "   $w" -ForegroundColor Yellow
    }
    Write-Host ""
}

if ($errors.Count -gt 0) {
    Write-Host "❌ Erreurs ($($errors.Count)):" -ForegroundColor Red
    foreach ($e in $errors) {
        Write-Host "   $e" -ForegroundColor Red
    }
    Write-Host ""
    Write-Host "⛔ Veuillez corriger les erreurs avant de pousser" -ForegroundColor Red
    exit 1
}

# Résumé
Write-Host "=========================================" -ForegroundColor Green
Write-Host "   ✅ Prêt à Pousser !                   " -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Prochaines étapes:" -ForegroundColor Cyan
Write-Host "1. Exécutez: .\deploy\push-to-github.ps1" -ForegroundColor White
Write-Host "2. Attendez que GitHub Actions construise l'image" -ForegroundColor White
Write-Host "3. Déployez sur le VPS avec deploy-image.sh" -ForegroundColor White
Write-Host ""

# Demander confirmation
$confirmation = Read-Host "Voulez-vous pousser maintenant? (o/N)"
if ($confirmation -eq "o" -or $confirmation -eq "O") {
    Write-Host ""
    Write-Host "[*] Exécution du push..." -ForegroundColor Yellow
    & ".\deploy\push-to-github.ps1"
} else {
    Write-Host ""
    Write-Host "Push annulé. Vous pouvez le faire plus tard avec:" -ForegroundColor Yellow
    Write-Host "  .\deploy\push-to-github.ps1" -ForegroundColor Gray
    Write-Host ""
}

