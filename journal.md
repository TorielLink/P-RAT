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
- **Horloge injectée (`java.time.Clock`)** : la date d'ouverture et de résolution vient du serveur ;
  en test, une `Clock.fixed` rend les seuils RG3 vérifiables (47 h / 48 h / 72 h).
- **Statuts RG2** : OUVERT = non affecté, EN_COURS = affecté (« Pris en charge »), RESOLU = résolu.
- **Durée et niveau d'attention calculés à chaque lecture, jamais stockés** : `Incident.dureeOuverture`
  et `NiveauAttention.de` (seuils 48 h / 72 h en constantes nommées, réutilisés par F11).
- **Gestion des exceptions : `@RestControllerAdvice` + `ProblemDetail`** plutôt que `ResponseStatusException` :
  traduction centralisée, services indépendants de HTTP, erreurs de validation renvoyées champ par champ.
- **`ResponseEntity`** uniquement pour la création (201 + en-tête `Location`).
- **Prestataire sur l'incident d'un autre : 404 plutôt que 403**, pour ne pas révéler l'existence
  des incidents des autres entreprises.
- **Validation en double** : Bean Validation côté serveur (seule garantie réelle), reprise dans Angular
  pour le confort (pas d'appel inutile, message immédiat).

## LOT ALPHA
### Fonctionnalité transverse 1 : …
- Besoin · périmètre envisagé · choix et essais · ce qui fonctionne · difficultés · reste à faire
### Fonctionnalité transverse 2 : …
## BILAN
- Contribution de chacun : …
- Plus grande difficulté et comment elle a été surmontée : …
- Fonctionnalités non réalisées et pourquoi : …
- Trois points à retenir du cours et du projet : …