# 📧 CONFIGURATION EMAIL GMAIL POUR ShopTracker

## Date : 28 Décembre 2025

---

## ❌ PROBLÈME ACTUEL

**Erreur :**
```
jakarta.mail.AuthenticationFailedException: 535-5.7.8 Username and Password not accepted
```

**Cause :** Gmail **refuse** l'authentification avec un mot de passe normal (`Sherine2011$`) pour des raisons de sécurité.

---

## ✅ SOLUTION : Mot de Passe d'Application Gmail

Google exige maintenant l'utilisation d'un **"Mot de passe d'application"** (App Password) pour les applications tierces comme Spring Boot.

---

## 🔧 ÉTAPES DE CONFIGURATION

### 1️⃣ Activer la Vérification en 2 Étapes

1. Allez sur : **https://myaccount.google.com/security**
2. Connectez-vous avec : `saasify.solutions.iq@gmail.com`
3. Cherchez la section **"Validation en deux étapes"** (2-Step Verification)
4. **Activez-la** si ce n'est pas déjà fait
   - Vous devrez ajouter votre numéro de téléphone
   - Confirmez l'activation

---

### 2️⃣ Générer un Mot de Passe d'Application

1. **Lien direct :** https://myaccount.google.com/apppasswords
   
   OU
   
   - Allez sur : https://myaccount.google.com/security
   - Cliquez sur **"Validation en deux étapes"**
   - Faites défiler vers le bas
   - Cliquez sur **"Mots de passe des applications"** (App Passwords)

2. **Sélectionnez l'application :**
   - Application : **"Autre (nom personnalisé)"**
   - Nom : **"ShopTracker Spring Boot"** ou **"TicketCompare App"**

3. **Cliquez sur "Générer"**

4. **Gmail génère un mot de passe à 16 caractères** comme :
   ```
   abcd efgh ijkl mnop
   ```

5. **IMPORTANT :** 
   - **Copiez ce mot de passe IMMÉDIATEMENT** (il ne sera plus affiché)
   - Enlevez les espaces : `abcdefghijklmnop`
   - Conservez-le en sécurité

---

### 3️⃣ Mettre à Jour application.properties

**Fichier :** `src/main/resources/application.properties`

Remplacez :
```properties
spring.mail.password=${MAIL_PASSWORD:Sherine2011$}
```

Par :
```properties
spring.mail.password=${MAIL_PASSWORD:abcdefghijklmnop}
```

**Exemple complet :**
```properties
# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=saasify.solutions.iq@gmail.com
spring.mail.password=abcdefghijklmnop
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# Désactiver le health check mail
management.health.mail.enabled=false

# Application Email Settings
app.mail.from=saasify.solutions.iq@gmail.com
app.base-url=http://localhost:8080
```

---

### 4️⃣ Redémarrer l'Application

```bash
# Arrêter Docker
docker-compose -f docker/docker-compose-h2.yml down

# Rebuild et redémarrer
docker-compose -f docker/docker-compose-h2.yml up --build -d

# Vérifier les logs
docker-compose -f docker/docker-compose-h2.yml logs -f
```

---

## ✅ VÉRIFICATION

### Test 1 : Inscription d'un utilisateur

1. Allez sur : http://localhost:8080/auth/register
2. Créez un compte
3. **Vérifiez les logs Docker :**
   ```
   Email de bienvenue envoyé à test@example.com
   ```

### Test 2 : Réception de l'email

1. **Vérifiez votre boîte de réception** (ou spam)
2. Vous devriez recevoir un email avec :
   - Sujet : **"Bienvenue sur ShopTracker ! 🎉"**
   - Lien de vérification d'email

---

## 🔒 SÉCURITÉ

### ⚠️ IMPORTANT : Ne JAMAIS commiter les mots de passe

Pour la **production**, utilisez des **variables d'environnement** :

**Créer un fichier `.env` (NON commité) :**
```env
MAIL_USERNAME=saasify.solutions.iq@gmail.com
MAIL_PASSWORD=abcdefghijklmnop
```

**Modifier application.properties :**
```properties
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
```

**Ajouter à `.gitignore` :**
```
.env
application-prod.properties
```

---

## 🔄 ALTERNATIVE : Utiliser un Service Email Dédié

Pour la **production**, considérez des services professionnels :

### 1. **SendGrid** (Recommandé)
- ✅ 100 emails/jour GRATUITS
- ✅ Facile à intégrer avec Spring Boot
- ✅ Statistiques et tracking
- 🔗 https://sendgrid.com

**Configuration SendGrid :**
```properties
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=VOTRE_SENDGRID_API_KEY
```

### 2. **Mailgun**
- ✅ 5000 emails/mois GRATUITS (3 premiers mois)
- 🔗 https://www.mailgun.com

### 3. **AWS SES** (Amazon Simple Email Service)
- ✅ 62 000 emails/mois GRATUITS (depuis EC2)
- 🔗 https://aws.amazon.com/ses/

---

## 🐛 DÉPANNAGE

### Erreur : "Username and Password not accepted"
✅ **Solution :** Utilisez un mot de passe d'application (voir étapes ci-dessus)

### Erreur : "Connection timeout"
✅ **Solution :** Vérifiez que le port 587 n'est pas bloqué par votre firewall

### Erreur : "530 5.7.0 Must issue a STARTTLS command first"
✅ **Solution :** Assurez-vous que `starttls.enable=true`

### Emails envoyés mais non reçus
✅ **Vérifiez :**
- Dossier SPAM
- Quota Gmail (500 emails/jour)
- Logs de l'application

---

## 📝 CHECKLIST COMPLÈTE

- [ ] Activer la vérification en 2 étapes sur Google
- [ ] Générer un mot de passe d'application Gmail
- [ ] Copier le mot de passe à 16 caractères (sans espaces)
- [ ] Mettre à jour `application.properties` avec le nouveau mot de passe
- [ ] Désactiver `management.health.mail.enabled=false`
- [ ] Redémarrer Docker
- [ ] Tester l'inscription d'un utilisateur
- [ ] Vérifier réception de l'email de bienvenue
- [ ] (Production) Déplacer les credentials vers des variables d'environnement

---

## 📞 SUPPORT

**Si ça ne fonctionne toujours pas :**

1. Vérifiez les logs Docker pour l'erreur exacte
2. Testez la connexion SMTP manuellement avec Telnet
3. Essayez avec un autre compte Gmail
4. Considérez SendGrid pour éviter les complications Gmail

---

**Date :** 28 Décembre 2025  
**Application :** 🧠 ShopTracker  
**Status :** ⚠️ EMAIL À CONFIGURER

