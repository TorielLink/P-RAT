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
import { Affectations } from './pages/affectations/affectations';
import { accueilGuard, dejaConnecteGuard, profilGuard } from './core/auth/profil.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', canActivate: [accueilGuard], children: [] },
      { path: 'tableau-de-bord', component: TableauDeBord, canActivate: [profilGuard('ADMIN', 'PRESTATAIRE')] },
      { path: 'stations', component: Stations },
      { path: 'stations/:id', component: StationPage },
      { path: 'equipements', component: Equipements },
      {
        path: 'equipements/:id/declarer-incident',
        component: NouvelIncident,
        canActivate: [profilGuard('ADMIN', 'AGENT')],
      },
      { path: 'equipements/:id', component: EquipementPage },
      { path: 'incidents', component: Incidents },
      { path: 'incidents/historique', component: Historique },
      { path: 'incidents/:id', component: IncidentPage },
      { path: 'affectations', component: Affectations, canActivate: [profilGuard('ADMIN')] },
      { path: 'login', component: LoginComponent, canActivate: [dejaConnecteGuard] }
    ],
  },
  // Adresse inconnue : retour à l'accueil (ou à la connexion si la session est absente)
  { path: '**', redirectTo: '' },
];
