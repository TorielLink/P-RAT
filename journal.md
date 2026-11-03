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

### Modification d'un incident (F6)

- **Principe** : on corrige ce que le déclarant a saisi ; on ne touche pas à ce que le serveur ou un
  autre processus a fixé.
  - Modifiables : description (obligatoire, 10 à 500 caractères, même validation que F4 grâce aux
    constantes de `DeclarationIncidentRequest`) et priorité (optionnelle).
  - Non modifiables : équipement (RG1), date d'ouverture (fixée par l'horloge serveur, base de RG3),
    statut et prestataire (F7, F8), date de résolution (F8, RG5).
- **Droits** : Admin sur tout incident non résolu ; Agent sur ses propres incidents encore non
  affectés (`OUVERT`) ; Prestataire jamais. Un incident résolu n'est modifiable par personne.
  La règle est dans le modèle (`Incident.estModifiablePar`) avec un `switch` exhaustif sur le profil :
  un nouveau profil oblige à décider de ses droits pour compiler.
- **Déclarant** : champ `utilisateur` de l'incident, renseigné à la déclaration (F4) ; aucune migration.
- **`PATCH /api/incidents/{id}`** plutôt que `PUT` : modification partielle des seuls champs autorisés.
- **Champs non modifiables ignorés (et non rejetés)** : `ModificationIncidentRequest` ne contient que
  `description` et `priorite` (liste blanche). Un champ envoyé en plus (`statut`, `prestataireId`…)
  n'existe pas dans le DTO et ne peut jamais atteindre l'entité. Vérifié par un test (`ArgumentCaptor`).
- **Ordre des contrôles** : incident inexistant (404) → incident résolu (409, RG5, pour tous les profils)
  → droits (403). Le Prestataire est refusé encore plus tôt, par `SecurityConfig` (RG11).
- **403 avec message** : `OperationInterditeException`, traduite par `GestionnaireExceptions`, plutôt que
  `AccessDeniedException` de Spring, qui renvoie un corps vide. L'interface affiche le message précis.
- **Indicateur `modifiable` dans le détail** : calculé par le serveur avec la même règle, il pilote le
  bouton « Modifier ». La règle n'est pas recopiée en TypeScript (une seule source de vérité) ; le
  serveur la réapplique si on force l'appel.
- **Pas de `save()` dans `modifierIncident`** : l'incident est chargé dans la transaction ; Hibernate
  détecte la modification (dirty checking) et écrit l'`UPDATE` à la fin de la méthode.
- **Changement de droit** : le Prestataire pouvait modifier ses incidents (ancien `PUT`) ; ce droit est
  retiré. Il ne corrige pas la déclaration, il intervient sur l'équipement (état) et sur la résolution.
- **Journal métier (alpha n°5)** : non retenu à ce stade. S'il l'est, `modifierIncident` est le point
  d'accroche unique pour tracer qui, quand, ancienne et nouvelle valeur.

### Affectation d'un prestataire (F7)

- **`PUT /api/incidents/{id}/prestataire`** (corps : `prestataireId`) : on remplace le prestataire de
  l'incident ; affecter et réaffecter sont la même opération. Admin uniquement (`SecurityConfig`).
- **Règle dans le modèle** : `Incident.affecterA` change ensemble le prestataire et le statut
  (`EN_COURS`, RG2) ; le niveau d'attention devient « Pris en charge » (RG4).
- **Même prestataire : sans effet** (200, rien ne change). Cohérent avec l'idempotence de `PUT` ;
  l'interface affiche « déjà affecté à X : aucune modification » sans appeler le serveur.
- **Prestataire inexistant : 404** (`PrestataireIntrouvableException`) : la ressource désignée n'existe
  pas ; même exception et même message que dans le reste de l'API.
- **Incident résolu : 409** (`IncidentDejaResoluException`, RG5). La vérification reste dans
  `obtenirIncidentNonResolu`, commun à la modification, à l'affectation et à la résolution.
- **Atomicité (RG10)** : tous les contrôles (incident, résolu, prestataire) ont lieu avant toute
  modification ; `@Transactional` sur `affecterPrestataire` annule en plus toute écriture si une
  exception survient (rollback sur `RuntimeException`).
- **Lot alpha n°3 (aide à l'affectation)** : la recommandation proposera un prestataire, mais
  l'affectation passera toujours par `IncidentService.affecterPrestataire`, pour ne jamais contourner
  RG5 et RG10.

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

### Documentation de l'API
- Swagger UI sur http://localhost:8080/swagger-ui.html
  (JSON OpenAPI : /v3/api-docs), généré par springdoc-openapi 3.x. Accessible après connexion
  sur l'application.

## LOT ALPHA
### Fonctionnalité transverse 1 : …
- Besoin · périmètre envisagé · choix et essais · ce qui fonctionne · difficultés · reste à faire
### Fonctionnalité transverse 2 : …

### Easter egg (bonus)
- Konami code (↑ ↑ ↓ ↓ ← → ← → B A) : une rame traverse l'écran au son d'un carillon.
- Front uniquement : aucune donnée modifiée, aucun appel serveur.
- `KonamiService` (logique pure : fenêtre glissante des 10 dernières touches) ;
  composant `EasterEgg` pour les effets (clavier, son Web Audio, animation CSS).
- Ignoré pendant la saisie dans un champ.


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