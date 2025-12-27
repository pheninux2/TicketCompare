# 🐧 Configuration Docker avec WSL 2

## Problème
```
The command 'docker' could not be found in this WSL 2 distro.
```

## 🎯 Solution 1 : Activer l'intégration WSL 2 (Recommandé)

### Étapes

1. **Ouvrir Docker Desktop sur Windows**
   - Cliquez sur l'icône Docker dans la barre des tâches
   - Ouvrez Docker Desktop

2. **Accéder aux paramètres**
   - Cliquez sur l'icône ⚙️ (Settings) en haut à droite
   - Ou clic droit sur l'icône Docker → Settings

3. **Activer WSL 2**
   - Allez dans **Resources** → **WSL Integration**
   - Activez **Enable integration with my default WSL distro**
   - Cochez votre distribution (probablement Ubuntu)
   - Cliquez sur **Apply & Restart**

4. **Vérifier dans WSL**
   ```bash
   # Fermer et rouvrir votre terminal WSL
   # Puis tester :
   docker --version
   docker-compose --version
   ```

### Capture d'écran des paramètres

```
Docker Desktop → Settings → Resources → WSL Integration

☑ Enable integration with my default WSL distro
☑ Ubuntu (ou votre distro)

[Apply & Restart]
```

---

## 🎯 Solution 2 : Utiliser Docker depuis Windows

Si l'intégration WSL ne fonctionne pas, utilisez PowerShell Windows :

### Dans PowerShell Windows

```powershell
# Naviguer vers votre projet
cd C:\Users\pheni\IdeaProjects\TicketCompare

# Lancer l'application
.\scripts\start-docker.bat
```

### Avantages
- ✅ Fonctionne immédiatement
- ✅ Pas de configuration WSL nécessaire
- ✅ Utilise les scripts .bat

---

## 🎯 Solution 3 : Installer Docker directement dans WSL

Si vous voulez Docker natif dans WSL (sans Docker Desktop) :

### Installation

```bash
# Mettre à jour les paquets
sudo apt update
sudo apt upgrade -y

# Installer les dépendances
sudo apt install -y apt-transport-https ca-certificates curl software-properties-common

# Ajouter la clé GPG de Docker
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# Ajouter le dépôt Docker
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Installer Docker
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# Ajouter votre utilisateur au groupe docker
sudo usermod -aG docker $USER

# Démarrer le service Docker
sudo service docker start

# Vérifier
docker --version
```

### Démarrage automatique de Docker

Ajoutez dans `~/.bashrc` ou `~/.zshrc` :

```bash
# Démarrer Docker automatiquement
if ! docker info > /dev/null 2>&1; then
    sudo service docker start > /dev/null 2>&1
fi
```

---

## 🎯 Solution 4 : Utiliser un alias vers Docker Windows

Si Docker Desktop est installé sur Windows, créez un alias :

```bash
# Ajouter dans ~/.bashrc
echo 'alias docker="/mnt/c/Program\ Files/Docker/Docker/resources/bin/docker.exe"' >> ~/.bashrc
echo 'alias docker-compose="/mnt/c/Program\ Files/Docker/Docker/resources/bin/docker-compose.exe"' >> ~/.bashrc

# Recharger
source ~/.bashrc

# Tester
docker --version
```

---

## ✅ Vérification

Après avoir appliqué une solution, testez :

```bash
# Version Docker
docker --version

# Docker Compose
docker-compose --version

# Info Docker
docker info

# Test simple
docker run hello-world
```

---

## 🚀 Une fois Docker configuré

### Utiliser les scripts shell dans WSL

```bash
cd /mnt/c/Users/pheni/IdeaProjects/TicketCompare

# Rendre les scripts exécutables
chmod +x scripts/*.sh

# Lancer l'application
./scripts/start-docker.sh
```

### Ou utiliser Docker Compose directement

```bash
cd /mnt/c/Users/pheni/IdeaProjects/TicketCompare/docker

# Lancer
docker-compose up -d

# Voir les logs
docker-compose logs -f

# Arrêter
docker-compose down
```

---

## 🐛 Problèmes Courants

### Erreur : Cannot connect to Docker daemon

```bash
# Vérifier si Docker Desktop est lancé (Windows)
# Ou démarrer Docker service (Linux natif)
sudo service docker start
```

### Erreur : Permission denied

```bash
# Ajouter votre utilisateur au groupe docker
sudo usermod -aG docker $USER

# Se reconnecter
newgrp docker
```

### Erreur : Docker daemon not running

**Solution :** Démarrez Docker Desktop sur Windows et attendez qu'il soit complètement démarré (icône verte).

---

## 📝 Recommandation

**Pour WSL 2 :** Utilisez la **Solution 1** (intégration Docker Desktop WSL 2)

**Avantages :**
- ✅ Officiellement supporté
- ✅ Meilleure performance
- ✅ Partage des images entre Windows et WSL
- ✅ Interface graphique Docker Desktop

**Pour Linux natif :** Utilisez la **Solution 3** (Docker natif dans WSL)

---

## 🆘 Besoin d'aide ?

Si aucune solution ne fonctionne :

1. **Vérifier que Docker Desktop est installé sur Windows**
   - Télécharger depuis : https://www.docker.com/products/docker-desktop

2. **Vérifier la version de WSL**
   ```powershell
   # Dans PowerShell Windows
   wsl --list --verbose
   ```
   Vous devriez voir WSL 2

3. **Utiliser Windows directement**
   ```powershell
   # PowerShell Windows
   cd C:\Users\pheni\IdeaProjects\TicketCompare
   .\scripts\start-docker.bat
   ```

---

**Documentation officielle :**
- https://docs.docker.com/desktop/wsl/
- https://docs.docker.com/desktop/settings/windows/#wsl-integration

