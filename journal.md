# Journal — P-RAT (ALTN72, 2026-27)
**Groupe :** Antoine Roucau, Clothilde Proux
## README
- Prérequis : Java …, Maven …, MySQL, NPM
- Initialiser la base : `db/create.sql` (ou procédure …)
- Lancer : `mvn spring-boot:run`
- Application déployée : https://…
- Comptes de test : profil 1 : login / mot de passe ; profil 2 : …
- Limites connues : …
## ARCHITECTURE
- Dépôt : https://…
- Schéma : ![architecture](docs/architecture.png)
## TESTS
- Stratégie : …
- Commande : `mvn verify`
- Couvert : … | Non couvert : … (pourquoi)
- Comptes de test : admin / demo123 ; prestataire / demo123 ; agent / demo123
## CHOIX TECHNIQUES
### Droits par profil : # Droits d'accès par profil

| N° | Fonctionnalité | Profil 1Superviseur | Profil 2Prestataire externe | Profil 3Agent de terrain |
| --- | --- | --- | --- | --- |
| F2–F3 | Consulter équipements et fiches | WL | L | WL |
| F4 | Déclarer un incident | WL | WL | WL |
| F5–F6 | Consulter / modifier un incident | WL | WL | WL |
| F7 | Affecter ou réaffecter un prestataire | WL | X | X |
| F8 | Déclarer un incident résolu | WL | X | X |
| F9 | Consulter l'historique | WL | L | L |
| F10 | Évaluer un prestataire | WL | X | X |
| F11 | Tableau de bord | WL | L | L |

WL : Lecture et écriture &nbsp;  L : Lecture seule &nbsp; X : Aucun accès

- Exceptions : Nous avons choisi comme systeme de gestion d'exeception d'utiliser @ControllerAdvice pour plusieurs point majeurs :
- La coherence de l'ecriture des exceptions
- La séparation des responsabilités
- Pas de duplication d'exception, centralisation sur un gestionnaire d'erreur et donc application des exceptions de facon transverse

- SOLID : principe(s) appliqué(s) (où, pourquoi) ; principes écartés (pourquoi)
D'un point de vue générale nous allons utiliser : 
SRP pour ...
ISP pour ...
DIP pour ...

Nous n'allons pas utiliser les deux autres principe suivant :
OCP parce que ...
ISP parce que ...

- Requête SQL native : où ? analyse critique
- @Transactional : où ? pourquoi ?

### Authentification
- **Session + cookie (JSESSIONID) plutôt que JWT** : avec le proxy Angular, front et back
  partagent la même origine ; le navigateur gère le cookie seul, la déconnexion invalide
  la session côté serveur. Un JWT aurait demandé de gérer stockage, expiration et révocation.
- **Hachage BCrypt** : algorithme lent et salé, recommandé par l'OWASP. Aucun mot de passe en clair
  en base ni dans Git.
- **API : 401 au lieu d'une redirection HTML** (`HttpStatusEntryPoint`) : le front Angular
  décide lui-même de l'affichage.
- **CSRF désactivé** : simplification assumée, atténuée par le cookie `SameSite=Lax`
  par défaut des navigateurs. Amélioration possible : `csrf.spa()` (Spring Security 6.5+).
- **Le guard Angular n'est qu'un confort d'affichage** : la restriction réelle est appliquée
  par Spring Security (RG11).

### Incidents

- **Horloge injectée (`java.time.Clock`)** : les dates d'ouverture et de résolution sont fixées par le
  serveur (`LocalDateTime.now(horloge)`), jamais par le client. Bean `HorlogeConfig` en production,
  `Clock.fixed(...)` en test : les seuils RG3 (47 h / 48 h / 72 h) deviennent vérifiables.
- **Statuts RG2 sans migration** : OUVERT = non affecté, EN_COURS = affecté (affiché « Pris en charge »),
  RESOLU = résolu.
- **Durée et niveau d'attention calculés à chaque lecture, jamais stockés** : `Incident.dureeOuverture`
  (figée à la date de résolution) et `NiveauAttention.de` dans le modèle. Les seuils 48 h / 72 h sont des
  constantes nommées définies une seule fois (`SEUIL_ORANGE`, `SEUIL_ROUGE`), réutilisées par F11.
  Angular ne fait que mettre en forme (« 2 j 3 h »).
- **URL de déclaration : `POST /api/equipements/{id}/incidents`** : l'incident est créé « pour un
  équipement donné » (F4) ; l'équipement est dans l'URL, donc non modifiable par le formulaire.
  L'endpoint reste dans `IncidentController` (`@RequestMapping("/api")`) pour regrouper toutes les
  opérations sur les incidents, y compris dans la documentation Swagger (F12).
- **DTO dédiés** : `DeclarationIncidentRequest` ne contient ni date, ni prestataire, ni équipement :
  le client ne peut pas les imposer (RG1). `IncidentDetailDto` réutilise `IncidentDto` grâce à
  `@JsonUnwrapped` (JSON plat, pas de duplication des champs).
- **Validation en double** : Bean Validation côté serveur (`@NotBlank`, `@Size(10, 500)`), seule
  garantie réelle ; reprise dans Angular pour le confort (pas d'appel inutile, message immédiat).
  Les erreurs serveur sont renvoyées champ par champ et affichées sous le champ concerné.
- **Ordre de traitement d'une requête** : Spring Security (401 / 403) → validation (400) → service (404).
  Un prestataire est refusé avant même la lecture de ses données : il ne peut pas apprendre si un
  équipement existe (RG11).

### Gestion des exceptions

- **`@RestControllerAdvice` + `ProblemDetail`** plutôt que `ResponseStatusException` : la traduction
  exception → code HTTP est centralisée dans `GestionnaireExceptions` ; les services lèvent des
  exceptions métier (`IncidentIntrouvableException`…) sans connaître HTTP ; toutes les erreurs ont le
  même format JSON (`detail` en français, `erreurs` par champ pour les 400).
- Correspondances : ressource introuvable → 404, conflit (référence en double, incident déjà résolu)
  → 409, données invalides ou JSON illisible → 400.
- Pas de gestionnaire générique pour `Exception` : il intercepterait les `AccessDeniedException`
  et transformerait les 403 en 500.
- **Prestataire sur l'incident d'un autre : 404 plutôt que 403**, pour ne pas révéler l'existence des
  incidents des autres entreprises.
- **`ResponseEntity`** uniquement quand le code ou les en-têtes diffèrent du cas standard :
  201 + `Location` à la création, 204 à la suppression.

### Transactions

- `@Transactional(readOnly = true)` sur `IncidentService` : toutes les lectures sans écriture accidentelle.
- `@Transactional` sur `declarerIncident` (RG10) : la vérification de l'équipement et l'insertion forment
  un tout ; si l'une échoue, rien n'est enregistré.

## LOT ALPHA
### Fonctionnalité transverse 1 : …
- Besoin · périmètre envisagé · choix et essais · ce qui fonctionne · difficultés · reste à faire
### Fonctionnalité transverse 2 : …
## BILAN
- Contribution de chacun : …
- Plus grande difficulté et comment elle a été surmontée :
  - **Flyway et les migrations modifiées** : une migration déjà appliquée ne doit plus jamais être
      modifiée (checksum mismatch sur V1 après une réécriture). Tout changement passe par une nouvelle
      migration `V<n>`.
  - **Flyway et DevTools** : une V4 créée vide pendant que le back tournait a été appliquée vide par le
    redémarrage automatique de DevTools (checksum 0). Règle du groupe : arrêter le back avant de créer
    ou de modifier une migration.
- Fonctionnalités non réalisées et pourquoi : …
- Trois points à retenir du cours et du projet : …