import { Routes } from '@angular/router';
import {LoginComponent} from './pages/login/login';
import {authGuard} from './core/auth/auth.guard';
import {TableauDeBord} from './pages/tableau-de-bord/tableau-de-bord';
import { Stations } from "./pages/stations/stations";

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    canActivate: [authGuard],
    children: [
      { path: 'tableau-de-bord', component: TableauDeBord },
      { path: 'stations', component: Stations },
      { path: '', pathMatch: 'full', redirectTo: 'stations' },
    ],
  },
  { path: '**', redirectTo: 'login' },
];
