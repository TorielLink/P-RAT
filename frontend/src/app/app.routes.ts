import { Routes } from '@angular/router';
import {LoginComponent} from './pages/login/login';
import {authGuard} from './core/auth/auth.guard';
import {TableauDeBord} from './pages/tableau-de-bord/tableau-de-bord';
import { Stations } from "./pages/stations/stations";
import { EquipementPage } from './pages/equipement/equipement';
import { StationPage } from './pages/station/station';
import { Equipements } from './pages/equipements/equipements';
import { IncidentPage } from './pages/incident/incident';
import { Incidents } from './pages/incidents/incidents';
import { NouvelIncident } from './pages/nouvel_incident/nouvel_incident';
import { Affectations } from './pages/affectations/affectations';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    canActivate: [authGuard],
    children: [
      { path: 'tableau-de-bord', component: TableauDeBord },
      { path: 'stations', component: Stations },
      { path: 'stations/:id', component: StationPage },
      { path: 'equipements/:id', component: EquipementPage },
      { path: '', pathMatch: 'full', redirectTo: 'stations' },
      { path: 'equipements', component: Equipements },
      { path: 'affectations', component: Affectations },
      { path: 'incidents', component: Incidents },
      { path: 'equipements/:id/declarer-incident', component: NouvelIncident },
      { path: 'incidents/:id', component: IncidentPage }
    ],
  },
  { path: '**', redirectTo: 'login' },
];
