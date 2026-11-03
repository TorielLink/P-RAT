import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TypeEquipement } from '../equipement/equipement.service';
import { NiveauAttention } from '../incident/incident.service';

/** Correspond à IncidentActifDto côté backend : niveau et durée calculés par le serveur à chaque requête. */
export interface IncidentActif {
  id: number;
  niveauAttention: Exclude<NiveauAttention, 'RESOLU'>;
  stationId: number | null;
  stationNom: string | null;
  equipementId: number;
  equipementReference: string | null;
  equipementDescription: string | null;
  equipementType: TypeEquipement;
  description: string | null;
  dureeOuvertureMinutes: number;
  prestataireId: number | null;
  prestataireNom: string | null;
}

@Injectable({ providedIn: 'root' })
export class TableauDeBordService {
  private readonly http = inject(HttpClient);

  obtenirIncidentsActifs(): Observable<IncidentActif[]> {
    return this.http.get<IncidentActif[]>('/api/tableau-de-bord/incidents');
  }
}
