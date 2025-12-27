#!/bin/bash
# ===================================
# Script de vérification de l'installation
# TicketCompare
# ===================================

echo ""
echo "========================================"
echo "   TicketCompare - Vérification"
echo "========================================"
echo ""

JAVA_OK=0
MAVEN_OK=0
DOCKER_OK=0
TESSERACT_OK=0
STRUCT_OK=1

# Vérifier Java
echo "[CHECK] Vérification de Java..."
if command -v java &> /dev/null; then
    echo "[OK] Java est installé"
    java -version 2>&1 | head -n 1
    JAVA_OK=1
else
    echo "[ERROR] Java n'est pas installé ou pas dans le PATH!"
    echo "        Téléchargez Java 21 depuis: https://adoptium.net/"
fi
echo ""

# Vérifier Maven
echo "[CHECK] Vérification de Maven..."
if [ -f ./mvnw ]; then
    echo "[OK] Maven Wrapper est disponible"
    MAVEN_OK=1
elif command -v mvn &> /dev/null; then
    echo "[OK] Maven est installé"
    mvn -v 2>&1 | head -n 1
    MAVEN_OK=1
else
    echo "[WARNING] Maven n'est pas installé (optionnel, Maven Wrapper sera utilisé)"
fi
echo ""

# Vérifier Docker
echo "[CHECK] Vérification de Docker..."
if command -v docker &> /dev/null; then
    echo "[OK] Docker est installé"
    docker --version

    # Vérifier Docker Compose
    if command -v docker-compose &> /dev/null; then
        echo "[OK] Docker Compose est installé"
        docker-compose --version
        DOCKER_OK=1
    else
        echo "[WARNING] Docker Compose n'est pas installé"
    fi
else
    echo "[WARNING] Docker n'est pas installé"
    echo "          Vous pouvez utiliser le mode développement local sans Docker"
fi
echo ""

# Vérifier Tesseract (optionnel)
echo "[CHECK] Vérification de Tesseract OCR (optionnel)..."
if command -v tesseract &> /dev/null; then
    echo "[OK] Tesseract est installé"
    tesseract --version 2>&1 | head -n 1
    TESSERACT_OK=1
else
    echo "[INFO] Tesseract n'est pas installé"
    echo "       L'application fonctionnera sans OCR"
    echo "       Installation:"
    echo "       - Ubuntu/Debian: sudo apt install tesseract-ocr tesseract-ocr-fra"
    echo "       - macOS: brew install tesseract tesseract-lang"
fi
echo ""

# Vérifier la structure du projet
echo "[CHECK] Vérification de la structure du projet..."
if [ ! -f "pom.xml" ]; then
    echo "[ERROR] Fichier pom.xml non trouvé!"
    echo "        Êtes-vous dans le bon dossier?"
    STRUCT_OK=0
else
    echo "[OK] pom.xml trouvé"
fi

if [ ! -d "src" ]; then
    echo "[ERROR] Dossier src/ non trouvé!"
    STRUCT_OK=0
else
    echo "[OK] Dossier src/ trouvé"
fi

if [ ! -d "docker" ]; then
    echo "[WARNING] Dossier docker/ non trouvé"
    STRUCT_OK=0
else
    echo "[OK] Dossier docker/ trouvé"
fi

if [ ! -d "scripts" ]; then
    echo "[WARNING] Dossier scripts/ non trouvé"
    STRUCT_OK=0
else
    echo "[OK] Dossier scripts/ trouvé"
fi
echo ""

# Résumé
echo "========================================"
echo "   RÉSUMÉ DE LA VÉRIFICATION"
echo "========================================"
echo ""

if [ $JAVA_OK -eq 1 ]; then
    echo "[OK] Java           : Installé"
else
    echo "[ERROR] Java        : Non installé - REQUIS!"
fi

if [ $MAVEN_OK -eq 1 ]; then
    echo "[OK] Maven          : Disponible"
else
    echo "[WARNING] Maven     : Non disponible - Maven Wrapper sera utilisé"
fi

if [ $DOCKER_OK -eq 1 ]; then
    echo "[OK] Docker         : Installé"
else
    echo "[WARNING] Docker    : Non installé - Mode dev local uniquement"
fi

if [ $TESSERACT_OK -eq 1 ]; then
    echo "[OK] Tesseract      : Installé"
else
    echo "[INFO] Tesseract    : Non installé - OCR non disponible"
fi

if [ $STRUCT_OK -eq 1 ]; then
    echo "[OK] Structure      : Valide"
else
    echo "[ERROR] Structure   : Problème détecté"
fi

echo ""
echo "========================================"
echo "   RECOMMANDATIONS"
echo "========================================"
echo ""

if [ $JAVA_OK -eq 0 ]; then
    echo "[!] Installez Java 21 pour continuer"
    echo "    URL: https://adoptium.net/"
    echo ""
fi

if [ $DOCKER_OK -eq 1 ]; then
    echo "[+] Vous pouvez utiliser Docker:"
    echo "    ./scripts/start-docker.sh"
    echo ""
else
    echo "[+] Vous pouvez utiliser le mode dev local:"
    echo "    ./scripts/start-dev.sh"
    echo ""
fi

if [ $TESSERACT_OK -eq 0 ]; then
    echo "[i] Pour activer l'OCR, installez Tesseract:"
    echo "    - Ubuntu/Debian: sudo apt install tesseract-ocr tesseract-ocr-fra"
    echo "    - macOS: brew install tesseract tesseract-lang"
    echo ""
fi

echo "========================================"
echo "   PROCHAINES ÉTAPES"
echo "========================================"
echo ""
echo "1. Consultez: docs/DEMARRAGE_RAPIDE.md"
echo "2. Lancez: ./scripts/start-docker.sh OU ./scripts/start-dev.sh"
echo "3. Accédez: http://localhost:8080"
echo ""

