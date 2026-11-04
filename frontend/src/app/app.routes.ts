import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login';
import { authGuard } from './core/auth/auth.guard';
import { TableauDeBord } from './pages/tableau-de-bord/tableau-de-bord';
import { Stations } from './pages/stations/stations';
import { StationPage } from './pages/station/station';
import { Equipements } from './pages/equipements/equipements';
import { EquipementPage } from './pages/equipement/equipement';
import { NouvelIncident } from './pages/nouvel_incident/nouvel_incident';
import { Incidents } from './pages/incidents/incidents';
import { Historique } from './pages/historique/historique';
import { IncidentPage } from './pages/incident/incident';
import { IncidentsParStatut } from './pages/incidents-statut/incidents-statut';
import { Affectations } from './pages/affectations/affectations';
import { Prestataires } from './pages/prestataires/prestataires';
import { PrestatairePage } from './pages/prestataire/prestataire';
import { accueilGuard, dejaConnecteGuard, profilGuard } from './core/auth/profil.guard';
import { Carte } from './pages/carte/carte';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', canActivate: [accueilGuard], children: [] },
      { path: 'tableau-de-bord', component: TableauDeBord, canActivate: [profilGuard('ADMIN', 'PRESTATAIRE')] },
      // Stations et liste des équipements : pas pour le prestataire
      { path: 'stations', component: Stations, canActivate: [profilGuard('ADMIN', 'AGENT')] },
      { path: 'stations/:id', component: StationPage, canActivate: [profilGuard('ADMIN', 'AGENT')] },
      { path: 'equipements', component: Equipements, canActivate: [profilGuard('ADMIN', 'AGENT')] },
      { path: 'carte', component: Carte, canActivate: [profilGuard('ADMIN')] },
      {
        path: 'equipements/:id/declarer-incident',
        component: NouvelIncident,
        canActivate: [profilGuard('ADMIN', 'AGENT')],
      },
      // Ouvert à tous : le serveur ne montre au prestataire que les équipements de ses incidents
      { path: 'equipements/:id', component: EquipementPage },
      { path: 'incidents', component: Incidents },
      { path: 'incidents/historique', component: Historique },
      // Avant 'incidents/:id' : sinon « non-affectes » serait pris pour un identifiant
      {
        path: 'incidents/non-affectes',
        component: IncidentsParStatut,
        data: { statut: 'OUVERT', titre: 'Incidents non affectés' },
      },
      {
        path: 'incidents/en-cours',
        component: IncidentsParStatut,
        data: { statut: 'EN_COURS', titre: 'Incidents pris en charge' },
      },
      { path: 'incidents/:id', component: IncidentPage },
      { path: 'affectations', component: Affectations, canActivate: [profilGuard('ADMIN')] },
      { path: 'prestataires', component: Prestataires, canActivate: [profilGuard('ADMIN', 'PRESTATAIRE')] },
      { path: 'prestataires/:id', component: PrestatairePage, canActivate: [profilGuard('ADMIN', 'PRESTATAIRE')] },
      { path: 'login', component: LoginComponent, canActivate: [dejaConnecteGuard] }
    ],
  },
  // Adresse inconnue : retour à l'accueil (ou à la connexion si la session est absente)
  { path: '**', redirectTo: '' },
];
