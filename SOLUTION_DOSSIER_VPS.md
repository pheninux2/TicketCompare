# ✅ SOLUTION AU PROBLÈME "No such file or directory"

## 🐛 Problème identifié

```
C:\WINDOWS\System32\OpenSSH\scp.exe: dest open "/opt/shoptracker/scripts/": No such file or directory
```

**Cause :** Le dossier `/opt/shoptracker/scripts` n'existe pas encore sur le VPS.

---

## ✅ SOLUTION 1 : Utiliser le script mis à jour

Le script `transfer-to-vps.ps1` a été **mis à jour automatiquement** pour créer le dossier avant le transfert.

### Relancez simplement :

```powershell
.\transfer-to-vps.ps1
```

Le script va maintenant :
1. ✅ Se connecter au VPS
2. ✅ Créer le dossier `/opt/shoptracker/scripts`
3. ✅ Transférer tous les fichiers

---

## ✅ SOLUTION 2 : Créer manuellement la structure

Si vous préférez créer la structure manuellement d'abord :

### Méthode A : Via SSH direct

```powershell
ssh -p 443 root@178.128.162.253 "mkdir -p /opt/shoptracker/scripts"
```

Puis relancez le transfert :
```powershell
.\transfer-to-vps.ps1
```

---

### Méthode B : Via script prepare-vps.sh

1. **Transférer le script de préparation** :
   ```powershell
   scp -P 443 C:\Users\pheni\IdeaProjects\TicketCompare\deploy\prepare-vps.sh root@178.128.162.253:/tmp/
   ```

2. **Exécuter le script sur le VPS** :
   ```powershell
   ssh -p 443 root@178.128.162.253 "chmod +x /tmp/prepare-vps.sh && /tmp/prepare-vps.sh"
   ```

3. **Relancer le transfert complet** :
   ```powershell
   .\transfer-to-vps.ps1
   ```

---

## 📋 Structure créée sur le VPS

```
/opt/shoptracker/
├── scripts/     ← Scripts de déploiement
├── data/        ← Données de l'application
├── backups/     ← Backups de la base de données
└── logs/        ← Logs de l'application
```

---

## 🚀 MÉTHODE RECOMMANDÉE

**Relancez simplement le script mis à jour :**

```powershell
cd C:\Users\pheni\IdeaProjects\TicketCompare
.\transfer-to-vps.ps1
```

**Cette fois, vous verrez :**
```
[*] Creation du dossier sur le VPS...
[OK] Dossier /opt/shoptracker/scripts cree/verifie sur le VPS

[*] Transfert en cours...
[*] Transfert de setup-vps.sh...
[OK] setup-vps.sh transfere
...
```

---

## ✅ Résultat

```
✅ Script mis à jour avec création du dossier
✅ Fichier prepare-vps.sh créé
✅ Prêt à relancer le transfert
```

**RELANCEZ MAINTENANT LE SCRIPT !** 🚀

