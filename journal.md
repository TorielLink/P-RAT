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
## LOT ALPHA
### Fonctionnalité transverse 1 : …
- Besoin · périmètre envisagé · choix et essais · ce qui fonctionne · difficultés · reste à faire
### Fonctionnalité transverse 2 : …
## BILAN
- Contribution de chacun : …
- Plus grande difficulté et comment elle a été surmontée : …
- Fonctionnalités non réalisées et pourquoi : …
- Trois points à retenir du cours et du projet : …