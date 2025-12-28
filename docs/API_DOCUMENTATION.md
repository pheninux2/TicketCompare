# 📚 DOCUMENTATION API REST - ShopTracker

## Version: 1.0.0
**Date:** 28 Décembre 2025

---

## 🌐 Base URL

```
Production: https://api.ShopTracker.com
Development: http://localhost:8080
```

---

## 🔐 Authentification

Toutes les requêtes API nécessitent une authentification via session cookie après connexion.

### Session Cookie
```
Cookie: JSESSIONID=xxx
```

---

## 📋 ENDPOINTS

### 1. AUTHENTIFICATION

#### POST /auth/register
Créer un nouveau compte utilisateur

**Request Body:**
```json
{
  "fullName": "Jean Dupont",
  "email": "jean.dupont@example.com",
  "password": "securePassword123",
  "confirmPassword": "securePassword123",
  "acceptTerms": true
}
```

**Response:** 302 Redirect to `/auth/login`

**Success Response:**
```json
{
  "success": true,
  "message": "Inscription réussie ! Vous avez 30 jours d'essai gratuit."
}
```

**Error Response:**
```json
{
  "success": false,
  "errors": {
    "email": "Cet email est déjà utilisé",
    "password": "Le mot de passe doit contenir au moins 8 caractères"
  }
}
```

---

#### POST /auth/login
Connexion utilisateur

**Request Body:**
```json
{
  "email": "jean.dupont@example.com",
  "password": "securePassword123",
  "rememberMe": true
}
```

**Response:** 302 Redirect to `/dashboard` or `/pricing`

**Success:** Creates session cookie

**Error Response:**
```json
{
  "success": false,
  "error": "Email ou mot de passe incorrect"
}
```

---

#### GET /auth/logout
Déconnexion utilisateur

**Response:** 302 Redirect to `/auth/login`

**Success Message:** "Vous avez été déconnecté avec succès"

---

#### GET /auth/verify-email?token={token}
Vérification d'email

**Parameters:**
- `token` (string, required): Token de vérification reçu par email

**Response:** 302 Redirect to `/auth/login`

**Success:** Email vérifié
**Error:** Token invalide ou expiré

---

### 2. LICENCES

#### GET /license
Afficher les détails de la licence de l'utilisateur connecté

**Response:**
```json
{
  "id": 1,
  "licenseType": "TRIAL",
  "licenseKey": "ShopTracker-A3F5D8B2-9C1E4F7A-2D6B8E9F",
  "startDate": "2025-12-01",
  "expiryDate": "2025-12-31",
  "status": "ACTIVE",
  "daysRemaining": 15,
  "autoRenew": false
}
```

---

#### POST /license/upgrade
Mettre à niveau la licence

**Request Body:**
```json
{
  "licenseType": "MONTHLY" // ou "YEARLY" ou "LIFETIME"
}
```

**Success Response:**
```json
{
  "success": true,
  "message": "Votre licence a été mise à niveau avec succès vers Abonnement mensuel",
  "license": {
    "id": 1,
    "licenseType": "MONTHLY",
    "expiryDate": "2026-01-28"
  }
}
```

---

#### POST /license/renew
Renouveler la licence

**Response:**
```json
{
  "success": true,
  "message": "Votre licence a été renouvelée jusqu'au 2026-02-28",
  "license": {
    "expiryDate": "2026-02-28"
  }
}
```

---

#### POST /license/cancel
Annuler la licence

**Response:**
```json
{
  "success": true,
  "message": "Votre licence a été annulée. Vous pouvez continuer à utiliser l'application jusqu'à la fin de votre période payée."
}
```

---

#### POST /license/auto-renew?enable={boolean}
Activer/Désactiver le renouvellement automatique

**Parameters:**
- `enable` (boolean, required): true pour activer, false pour désactiver

**Response:**
```json
{
  "success": true,
  "message": "Le renouvellement automatique a été activé"
}
```

---

### 3. TARIFICATION

#### GET /pricing
Afficher la page de tarification avec tous les plans

**Response:** HTML page with all plans

---

#### POST /pricing/select
Sélectionner un plan

**Request Body:**
```json
{
  "planType": "MONTHLY" // ou "YEARLY" ou "LIFETIME"
}
```

**Response:** 302 Redirect to `/payment/checkout`

---

### 4. PAIEMENT

#### POST /payment/process
Traiter un paiement (intégration Stripe)

**Request Body:**
```json
{
  "token": "tok_visa_***", // Token Stripe
  "email": "jean.dupont@example.com",
  "planType": "MONTHLY"
}
```

**Success Response:**
```json
{
  "success": true,
  "chargeId": "ch_***",
  "amount": 4.99,
  "currency": "eur"
}
```

**Error Response:**
```json
{
  "success": false,
  "error": "Paiement refusé",
  "details": "Carte expirée"
}
```

---

#### GET /payment/success
Page de confirmation de paiement

**Response:** HTML page with success message

---

#### GET /payment/cancel
Page d'annulation de paiement

**Response:** HTML page with cancellation message

---

### 5. DASHBOARD

#### GET /dashboard
Tableau de bord utilisateur

**Response:** HTML page with user dashboard

**Data includes:**
```json
{
  "user": {
    "fullName": "Jean Dupont",
    "email": "jean.dupont@example.com"
  },
  "license": {
    "type": "MONTHLY",
    "daysRemaining": 25,
    "isExpiringSoon": false
  },
  "statistics": {
    "totalTickets": 150,
    "totalSpent": 1250.50,
    "averagePerMonth": 250.10
  }
}
```

---

#### GET /profile
Profil utilisateur

**Response:** HTML page with user profile

---

### 6. WEBHOOKS

#### POST /webhooks/stripe
Webhook Stripe pour les événements de paiement

**Headers:**
```
Stripe-Signature: xxx
```

**Request Body:** Stripe Event Object

**Events handled:**
- `payment_intent.succeeded`
- `payment_intent.failed`
- `customer.subscription.created`
- `customer.subscription.updated`
- `customer.subscription.deleted`

**Response:**
```json
{
  "received": true
}
```

---

## 📊 CODES DE STATUT HTTP

| Code | Description |
|------|-------------|
| 200  | OK - Requête réussie |
| 201  | Created - Ressource créée |
| 302  | Redirect - Redirection |
| 400  | Bad Request - Données invalides |
| 401  | Unauthorized - Non authentifié |
| 403  | Forbidden - Licence expirée |
| 404  | Not Found - Ressource introuvable |
| 500  | Internal Server Error - Erreur serveur |

---

## 🔒 SÉCURITÉ

### Authentification
- Les mots de passe sont hashés avec BCrypt (10 rounds)
- Les tokens de vérification expirent après 7 jours
- Les sessions expirent après 30 minutes d'inactivité

### Protection
- CSRF Protection (à activer en production)
- Rate Limiting sur les endpoints sensibles
- Validation des entrées avec Bean Validation
- SQL Injection protection via JPA/Hibernate

---

## 📈 RATE LIMITING

| Endpoint | Limite |
|----------|--------|
| POST /auth/register | 5 par heure par IP |
| POST /auth/login | 10 par heure par IP |
| POST /payment/process | 3 par minute par utilisateur |

---

## 🧪 ENVIRONNEMENTS

### Development
```
Base URL: http://localhost:8080
Stripe: Test Mode (pk_test_***)
Email: SMTP Test
Database: H2 in-memory
```

### Production
```
Base URL: https://api.ShopTracker.com
Stripe: Live Mode (pk_live_***)
Email: Production SMTP
Database: PostgreSQL
```

---

## 📞 SUPPORT

**Email:** support@ShopTracker.com  
**Documentation:** https://docs.ShopTracker.com  
**Status Page:** https://status.ShopTracker.com  

---

## 🆕 CHANGELOG

### Version 1.0.0 (28/12/2025)
- ✅ Authentification complète (inscription, connexion, vérification email)
- ✅ Gestion des licences (essai, mensuel, annuel, à vie)
- ✅ Intégration Stripe pour les paiements
- ✅ Emails automatiques (bienvenue, rappels, confirmations)
- ✅ Tâches planifiées (renouvellements, rappels)
- ✅ Dashboard utilisateur
- ✅ Intercepteur de licences actives

---

**Date de dernière mise à jour:** 28 Décembre 2025  
**Version de l'API:** 1.0.0  
**Application:** 🧠 ShopTracker - Smart Receipt Intelligence

