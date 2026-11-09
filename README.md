# Gestion intelligente des logs

Projet académique de 4ème année ingénierie informatique et réseaux.

Application qui **collecte, normalise, stocke, analyse et visualise** les logs système afin de détecter des
comportements anormaux. La source des logs est **Red Hat Enterprise Linux (RHEL)** uniquement.

## Architecture

```
RHEL (VM VMware)
  └─ rsyslog ──TCP 5514──► SyslogTcpReceiver
                                │
                                ▼
                          LogNormalizer  (RFC 3164 / RFC 5424)
                                │
                                ▼
                            LogEvent
                                │
                                ▼
                        LogIngestionService ──► RuleDetectionService (règles textuelles)
                                │
                                ▼
                        MongoLogRepository ──► MongoDB  (gestion_logs.logs)
```

## Technologies

| Composant | Technologie |
|---|---|
| Backend | Java 25, Maven |
| Base de données | MongoDB 9.0.4 (driver `mongodb-driver-sync` 5.5.1) |
| Collecte | Syslog / rsyslog, TCP |
| Source des logs | Red Hat Enterprise Linux (RHEL) |
| Frontend (prévu) | React |
| Communication (prévue) | API REST |
| IDE | Eclipse |

## État d'avancement

| Phase | Contenu | État |
|---|---|---|
| 1 | Réception Syslog, normalisation, ingestion, persistance MongoDB | Terminée |
| 2 | Détection statistique des anomalies | À développer (voir le cahier des charges) |
| 3 | API REST et authentification | À développer |
| 4 | Frontend React | À développer |
| 5 | Tests et validation | À développer |
| 6 | Documentation et rapport | À développer |

## Documentation

- [Guide d'installation et de mise en route](docs/GUIDE_INSTALLATION.pdf) : de l'installation de l'environnement
  jusqu'à la chaîne complète RHEL → MongoDB.
- [Cahier des charges de la détection statistique des anomalies](docs/CAHIER_DES_CHARGES_ANOMALIES.pdf).

## Démarrage rapide

Le détail complet (réseau, pare-feu, rsyslog) est dans le guide d'installation.

1. Installer : JDK 25, Eclipse (2025-12 ou plus récent), Git, MongoDB 9.0.4 et mongosh.
2. Cloner le dépôt et ouvrir le projet dans Eclipse (**File → Import → Maven → Existing Maven Projects**).
3. Compiler : **Run As → Maven build…**, goal `clean package`.
4. Démarrer MongoDB, puis lancer `SyslogTcpReceiver` (**Run As → Java Application**).
5. Configurer rsyslog sur la VM RHEL pour envoyer les logs vers le port TCP 5514 de la machine Windows.
6. Envoyer un log de test depuis RHEL et vérifier dans MongoDB :

```bash
# sur RHEL
logger -p user.err "TEST ERROR depuis RHEL"
```

```javascript
// dans mongosh
use gestion_logs
db.logs.find().sort({receivedTimestamp:-1}).limit(3)
```

> Maven n'est pas nécessaire dans le PATH : on utilise le Maven intégré à Eclipse.

## Structure du projet

```
src/main/java/ma/gestionlogs/
├── SyslogTcpReceiver.java        réception TCP (port 5514), un thread par client
├── LogNormalizer.java            analyse RFC 3164 / RFC 5424 → LogEvent
├── LogNormalizerTest.java        tests manuels du parseur
├── FunctionalTest.java           démonstration des interfaces fonctionnelles
├── MongoConnectionTest.java      test de connexion MongoDB
├── MongoLogRepositoryTest.java   test du stockage MongoDB (base gestion_logs_test)
├── model/        LogEvent, DetectionRule
├── repository/   LogRepository, MongoLogRepository, InMemoryLogRepository,
│                 RuleRepository, InMemoryRuleRepository, MongoConnection
├── service/      LogIngestionService, RuleDetectionService
└── functional/   AnomalyScorer, EventPredicate, LogProcessor
```

## Configuration par défaut

| Paramètre | Valeur | Où |
|---|---|---|
| Port TCP du récepteur | `5514` | constante `PORT` de `SyslogTcpReceiver` |
| Connexion MongoDB | `mongodb://localhost:27017` | `MongoConnection` |
| Base / collection | `gestion_logs` / `logs` | `MongoConnection` |

## Tests

Il n'y a pas encore de tests JUnit. Les tests existants sont des programmes à lancer avec
**Run As → Java Application** : `LogNormalizerTest`, `MongoConnectionTest`, `MongoLogRepositoryTest`.

## Travailler en équipe

- `main` : version stable ; pas de commit direct.
- `integration-logs` : branche d'intégration actuelle.
- `feature/<sujet>` : une branche par fonctionnalité (ex. `feature/anomaly-detection`), puis Pull Request.
- Ne jamais versionner `target/`, les fichiers `*.bak`, les mots de passe ou les jetons.
- Prévenir l'administrateur avant de modifier une classe partagée (`LogRepository`, `SyslogTcpReceiver`, `pom.xml`).

## Équipe

| Rôle | Nom |
|---|---|
| Administrateur du groupe, pipeline d'ingestion et stockage | _à compléter_ |
| Détection statistique des anomalies | _à compléter_ |
| API REST et authentification | _à compléter_ |
| Frontend React | _à compléter_ |
