# Configuration Spring Boot pour Reverse Proxy

## Problème

Lorsque vous utilisez un reverse proxy Nginx avec un préfixe de chemin (ex: `/app1/`), Spring Boot doit être configuré pour:
1. Accepter les requêtes avec les headers `X-Forwarded-*`
2. Gérer correctement les redirections et les URLs

## Solution

### Option 1: application.yml (Recommandé)

```yaml
server:
  port: 8080
  servlet:
    context-path: /
  forward-headers-strategy: framework
  tomcat:
    # Configuration pour reverse proxy
    remoteip:
      remote-ip-header: X-Forwarded-For
      protocol-header: X-Forwarded-Proto
      internal-proxies: 10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|172\\.(1[6-9]|2\\d|3[01])\\.\\d{1,3}\\.\\d{1,3}|192\\.168\\.\\d{1,3}\\.\\d{1,3}|100\\.([6-9]\\d|1[0-2]\\d)\\.\\d{1,3}\\.\\d{1,3}

spring:
  mvc:
    servlet:
      path: /
```

### Option 2: application.properties

```properties
server.port=8080
server.servlet.context-path=/
server.forward-headers-strategy=framework

# Tomcat reverse proxy configuration
server.tomcat.remoteip.remote-ip-header=X-Forwarded-For
server.tomcat.remoteip.protocol-header=X-Forwarded-Proto
server.tomcat.remoteip.internal-proxies=10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|172\\.(1[6-9]|2\\d|3[01])\\.\\d{1,3}\\.\\d{1,3}|192\\.168\\.\\d{1,3}\\.\\d{1,3}|100\\.([6-9]\\d|1[0-2]\\d)\\.\\d{1,3}\\.\\d{1,3}

spring.mvc.servlet.path=/
```

## Configuration Nginx correspondante

```nginx
location /app1/ {
    # Retirer le préfixe /app1 avant de proxy
    rewrite ^/app1/(.*) /$1 break;
    
    proxy_pass http://localhost:8080/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_set_header X-Forwarded-Prefix /app1;
    
    # WebSocket support
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";
}
```

## Pourquoi context-path = / ?

- Nginx utilise `rewrite` pour retirer le préfixe `/app1/`
- Spring Boot reçoit les requêtes sans préfixe
- Le header `X-Forwarded-Prefix` permet à Spring de connaître le préfixe d'origine

**Exemple:**
- Client demande: `http://178.128.162.253/app1/api/tickets`
- Nginx envoie à Spring: `http://localhost:8080/api/tickets`
- Spring voit le header: `X-Forwarded-Prefix: /app1`

## Gestion des URLs dans le code

### Thymeleaf (Templates)

Utilisez toujours `@{/...}` pour les URLs:

```html
<!-- ✅ Correct - génère /app1/api/tickets -->
<a th:href="@{/api/tickets}">Tickets</a>

<!-- ❌ Incorrect - génère /api/tickets -->
<a href="/api/tickets">Tickets</a>
```

### RestController (API)

Les URLs sont automatiquement gérées:

```java
@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    
    // Accessible via: /app1/api/tickets
    @GetMapping
    public List<Ticket> getAll() {
        return ticketService.findAll();
    }
}
```

### Redirection

```java
@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        // ✅ Correct - redirige vers /app1/dashboard
        return "redirect:/dashboard";
    }
    
    @GetMapping("/old-path")
    public RedirectView oldPath() {
        // ✅ Correct - utilise le contexte actuel
        return new RedirectView("/new-path");
    }
}
```

## Test de la configuration

### 1. Tester les headers

```bash
curl -v http://178.128.162.253/app1/actuator/health
```

Vérifiez que Spring reçoit les headers:
```
X-Forwarded-For: votre-ip
X-Forwarded-Proto: http
X-Forwarded-Prefix: /app1
```

### 2. Activer les logs de debug

Dans `application.yml`:

```yaml
logging:
  level:
    org.springframework.web: DEBUG
    org.springframework.security: DEBUG
```

### 3. Endpoint de test

Créez un endpoint pour vérifier les headers:

```java
@RestController
public class ProxyTestController {
    
    @GetMapping("/api/proxy-test")
    public Map<String, Object> proxyTest(HttpServletRequest request) {
        Map<String, Object> info = new HashMap<>();
        info.put("requestURL", request.getRequestURL().toString());
        info.put("contextPath", request.getContextPath());
        info.put("servletPath", request.getServletPath());
        info.put("forwardedFor", request.getHeader("X-Forwarded-For"));
        info.put("forwardedProto", request.getHeader("X-Forwarded-Proto"));
        info.put("forwardedPrefix", request.getHeader("X-Forwarded-Prefix"));
        return info;
    }
}
```

Test:
```bash
curl http://178.128.162.253/app1/api/proxy-test
```

Résultat attendu:
```json
{
  "requestURL": "http://localhost:8080/api/proxy-test",
  "contextPath": "",
  "servletPath": "/api/proxy-test",
  "forwardedFor": "votre-ip",
  "forwardedProto": "http",
  "forwardedPrefix": "/app1"
}
```

## Sécurité avec Spring Security

Si vous utilisez Spring Security, configurez-le pour accepter les headers:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Configuration standard
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register").permitAll()
                .anyRequest().authenticated()
            )
            // Important: accepter les headers de forwarding
            .headers(headers -> headers
                .frameOptions().sameOrigin()
            );
        
        return http.build();
    }
}
```

## Résolution de problèmes courants

### Les CSS/JS ne se chargent pas

**Problème:** Les ressources statiques ont des URLs incorrectes.

**Solution:** Utilisez `@{/...}` dans Thymeleaf:

```html
<!-- ✅ Correct -->
<link th:href="@{/css/style.css}" rel="stylesheet">
<script th:src="@{/js/app.js}"></script>

<!-- ❌ Incorrect -->
<link href="/css/style.css" rel="stylesheet">
```

### Les redirections ne fonctionnent pas

**Problème:** Spring redirige vers `/path` au lieu de `/app1/path`.

**Solution:** Vérifiez que `server.forward-headers-strategy=framework` est configuré.

### CORS errors

**Problème:** Les requêtes AJAX échouent avec des erreurs CORS.

**Solution:** Configurez CORS:

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://178.128.162.253")
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowedHeaders("*")
                    .allowCredentials(true);
            }
        };
    }
}
```

### Sessions ne fonctionnent pas

**Problème:** Les utilisateurs sont déconnectés aléatoirement.

**Solution:** Configurez les cookies avec le bon path:

```yaml
server:
  servlet:
    session:
      cookie:
        path: /
        http-only: true
        secure: false  # true si HTTPS
```

## Checklist de validation

- [ ] `server.forward-headers-strategy=framework` configuré
- [ ] Tous les templates utilisent `@{/...}` pour les URLs
- [ ] Test avec `curl -v` montre les headers corrects
- [ ] Les CSS/JS se chargent correctement
- [ ] Les redirections fonctionnent
- [ ] Les API REST répondent
- [ ] Les sessions persistent correctement

## Ressources

- [Spring Boot Behind a Proxy](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.webserver.use-behind-a-proxy-server)
- [Nginx Reverse Proxy](https://docs.nginx.com/nginx/admin-guide/web-server/reverse-proxy/)

