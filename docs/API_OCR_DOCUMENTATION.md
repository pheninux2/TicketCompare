# 🔌 API OCR REST - Documentation

## 📍 Base URL
```
http://localhost:8080/api/v1/tickets/ocr
```

## ✅ Health Check

### Vérifier l'état du service OCR

```bash
GET /api/v1/tickets/ocr/health
```

**Réponse:**
```json
{
    "status": "ok",
    "service": "OCR API",
    "version": "1.0",
    "available": true
}
```

---

## 📤 Upload et Analyse

### 1. Analyser une image uploadée

```bash
POST /api/v1/tickets/ocr/analyze/upload
Content-Type: multipart/form-data

Paramètres:
- file (required): L'image du ticket
- store (optional): Nom du magasin
- notes (optional): Notes additionnelles
- autoSave (optional, default: false): Sauvegarder automatiquement
```

**Exemple avec cURL:**
```bash
curl -X POST \
  -F "file=@ticket.jpg" \
  -F "store=Carrefour" \
  -F "notes=Courses hebdomadaires" \
  -F "autoSave=true" \
  http://localhost:8080/api/v1/tickets/ocr/analyze/upload
```

**Réponse (autoSave=false):**
```json
{
    "status": "success",
    "message": "Ticket analysé",
    "data": {
        "id": null,
        "date": "2025-12-20",
        "store": "Carrefour",
        "totalAmount": 45.50,
        "notes": "Courses hebdomadaires",
        "products": [
            {
                "id": null,
                "name": "Lait Demi-Écrémé",
                "category": "Laitier",
                "price": 1.50,
                "quantity": 2,
                "unit": "L",
                "totalPrice": 3.00
            },
            {
                "id": null,
                "name": "Pain Complet",
                "category": "Boulangerie",
                "price": 2.50,
                "quantity": 1,
                "unit": "u",
                "totalPrice": 2.50
            }
        ]
    }
}
```

**Réponse (autoSave=true):**
```json
{
    "status": "success",
    "message": "Ticket analysé et créé",
    "data": {
        "id": 5,
        "date": "2025-12-20",
        "store": "Carrefour",
        "totalAmount": 45.50,
        "notes": "Courses hebdomadaires",
        "createdAt": "2025-12-20",
        "products": [
            {
                "id": 32,
                "name": "Lait Demi-Écrémé",
                "category": "Laitier",
                "price": 1.50,
                "quantity": 2,
                "unit": "L",
                "totalPrice": 3.00
            }
        ]
    }
}
```

---

### 2. Analyser une image depuis un chemin local

```bash
POST /api/v1/tickets/ocr/analyze/path

Paramètres:
- imagePath (required): Chemin complet du fichier
- store (optional): Nom du magasin
- notes (optional): Notes additionnelles
- autoSave (optional, default: false): Sauvegarder automatiquement
```

**Exemple avec cURL:**
```bash
curl -X POST \
  -d "imagePath=C:\Users\pheni\Documents\ticket.jpg&store=Lidl&autoSave=true" \
  http://localhost:8080/api/v1/tickets/ocr/analyze/path
```

**Réponse:** Identique à `/analyze/upload`

---

### 3. Analyser et Créer en une seule requête

```bash
POST /api/v1/tickets/ocr/analyze-and-create
Content-Type: multipart/form-data

Paramètres:
- file (required): L'image du ticket
- store (optional): Nom du magasin
- notes (optional): Notes additionnelles

Note: Sauvegarde automatiquement le ticket
```

**Exemple avec cURL:**
```bash
curl -X POST \
  -F "file=@ticket.jpg" \
  -F "store=Monoprix" \
  http://localhost:8080/api/v1/tickets/ocr/analyze-and-create
```

**Réponse:**
```json
{
    "status": "success",
    "message": "Ticket créé avec succès",
    "data": {
        "id": 5,
        "date": "2025-12-20",
        "store": "Monoprix",
        "totalAmount": 45.50,
        "products": [...]
    },
    "productsCount": 8
}
```

---

## 🔧 Exemples d'Utilisation

### Python
```python
import requests

# Upload et analyse
files = {'file': open('ticket.jpg', 'rb')}
data = {'store': 'Carrefour', 'autoSave': 'true'}

response = requests.post(
    'http://localhost:8080/api/v1/tickets/ocr/analyze/upload',
    files=files,
    data=data
)

ticket = response.json()['data']
print(f"Ticket créé: {ticket['id']}")
print(f"Montant: {ticket['totalAmount']}€")
print(f"Produits: {len(ticket['products'])}")
```

### JavaScript/Node.js
```javascript
const FormData = require('form-data');
const fs = require('fs');
const axios = require('axios');

const form = new FormData();
form.append('file', fs.createReadStream('ticket.jpg'));
form.append('store', 'Carrefour');
form.append('autoSave', 'true');

axios.post('http://localhost:8080/api/v1/tickets/ocr/analyze/upload', form, {
    headers: form.getHeaders()
})
.then(res => {
    console.log('Ticket:', res.data.data);
})
.catch(err => console.error(err));
```

### bash/Shell
```bash
#!/bin/bash

# Analyse simple
curl -X POST \
  -F "file=@$1" \
  -F "store=Carrefour" \
  -F "autoSave=true" \
  http://localhost:8080/api/v1/tickets/ocr/analyze/upload | jq .

# Alternativement avec wget
wget --post-file=ticket.jpg \
  --header="Content-Type: image/jpeg" \
  http://localhost:8080/api/v1/tickets/ocr/analyze/upload
```

---

## ❌ Codes d'Erreur

| Code | Message | Cause |
|------|---------|-------|
| 400 | Fichier vide | Aucun fichier fourni |
| 400 | Le fichier doit être une image | Type MIME incorrect |
| 404 | Fichier non trouvé | Chemin inexistant |
| 500 | Erreur lors de l'OCR | Tesseract ne répond pas |

**Exemple de réponse d'erreur:**
```json
{
    "status": "error",
    "message": "Erreur lors de l'OCR: Tesseract not found"
}
```

---

## 📋 Format des Paramètres

### store (optionnel)
```
Format: String
Exemples: "Carrefour", "Lidl", "Monoprix", "Leclerc"
```

### notes (optionnel)
```
Format: String
Exemples: "Courses hebdomadaires", "Marché", "Promos"
```

### autoSave (optionnel)
```
Format: boolean (true/false)
Défaut: false
Si true: Le ticket est sauvegardé automatiquement en BD
Si false: Retourne seulement les données analysées
```

### imagePath (local path)
```
Format: String (chemin absolu)
Windows: "C:\Users\pheni\Documents\ticket.jpg"
Linux: "/home/user/documents/ticket.jpg"
macOS: "/Users/user/Documents/ticket.jpg"
```

---

## 🎯 Use Cases

### Use Case 1: Batch Processing
Analyser plusieurs tickets sans les sauvegarder immédiatement

```bash
for file in *.jpg; do
  curl -X POST \
    -F "file=@$file" \
    http://localhost:8080/api/v1/tickets/ocr/analyze/upload > "result_$file.json"
done
```

### Use Case 2: Integration avec App Mobile
Envoyer la photo de la caméra mobile

```bash
POST /api/v1/tickets/ocr/analyze-and-create
Content-Type: multipart/form-data

file: [image_binaire]
store: "Carrefour"
```

### Use Case 3: Traitement Serveur
Analyser les fichiers sauvegardés sur le disque

```bash
curl -X POST \
  -d "imagePath=/var/tickets/archive/ticket_2025_01.jpg&autoSave=true" \
  http://localhost:8080/api/v1/tickets/ocr/analyze/path
```

---

## 📊 Limitations

- **Taille maximale:** 10 MB par fichier
- **Formats supportés:** JPG, PNG, BMP, GIF, TIFF, WebP
- **Timeout:** 60 secondes par requête
- **Rate limit:** Aucune (pour développement)

---

## 🔒 Sécurité

⚠️ **En production:**
- Activer l'authentification API (JWT/OAuth)
- Valider les tailles de fichiers
- Implémenter un rate limiting
- Utiliser HTTPS

---

## 🐛 Débogage

Activer les logs détaillés:

```properties
# application.properties
logging.level.pheninux.xdev.ticketcompare=DEBUG
logging.level.net.sourceforge.tess4j=DEBUG
```

---

**Besoin d'aide ?** Consultez SCANNER_GUIDE.md pour l'utilisation UI.

