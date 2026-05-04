# PokeRixe — Backend

Backend de **PokeRixe**, un jeu de combat Pokémon compétitif en ligne avec analyse IA des parties.

Frontend : [github.com/BaptouK/PokeRixe](https://github.com/BaptouK/PokeRixe)

---

## Fonctionnalités

- Combats Pokémon en temps réel via **WebSocket**
- Authentification par **JWT** (inscription / connexion)
- Données Pokémon récupérées depuis **PokéAPI**
- Analyse IA des parties à la fin de chaque combat (via **Ollama**)
- Historique des parties par joueur
- API REST documentée avec **Swagger UI**

---

## Stack technique

| Couche | Technologie |
|---|---|
| Langage | Java 21 |
| Framework | Spring Boot 4 |
| Base de données | MongoDB 7 |
| Temps réel | WebSocket (MessagePack) |
| Sécurité | Spring Security + JWT |
| IA | Spring AI + Ollama |
| API externe | PokéAPI |

---

## Prérequis

- **Java 21**
- **Maven** (ou utiliser le wrapper `./mvnw`)
- **Docker & Docker Compose** (pour MongoDB)
- **Ollama** installé et démarré localement — [ollama.com](https://ollama.com)

---

## Installation et lancement

### 1. Cloner le projet

```bash
git clone https://github.com/Pokerixe/PokeRixe-Backend.git
cd PokeRixe-Backend
```

### 2. Démarrer MongoDB

```bash
docker compose -f local.docker-compose.yml up -d
```

Cela lance MongoDB sur le port `27017` et Mongo Express (interface web) sur le port `8081`.

### 3. Configurer Ollama

Ollama doit être installé séparément et un modèle doit être disponible localement.

```bash
ollama pull <nom-du-modele>
ollama serve
```

### 4. Configurer les variables d'environnement

Créer un fichier `.env.local` (ou définir les variables dans votre environnement) :

```env
MONGO_URI=mongodb://admin:adminpassword@localhost:27017/pokerixe?authSource=admin
JWT_SECRET=une_cle_secrete_dau_moins_32_caracteres
OLLAMA_MODEL=<nom-du-modele>
OLLAMA_PROMPT_FILE_PATH=./prompt.txt
```

### 5. Lancer l'application

```bash
./mvnw spring-boot:run
```

L'API est disponible sur `http://localhost:8080`.

---

## Documentation API

Une fois l'application démarrée, la documentation Swagger est accessible à :

```
http://localhost:8080/swagger-ui.html
```

---

## Monitoring

Des métriques Prometheus sont exposées via Spring Actuator :

```
http://localhost:8080/actuator/prometheus
```
