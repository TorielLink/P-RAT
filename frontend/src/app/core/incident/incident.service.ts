import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export const PRIORITES_INCIDENT = ['BASSE', 'MOYENNE', 'HAUTE', 'CRITIQUE'] as const;
export type PrioriteIncident = (typeof PRIORITES_INCIDENT)[number];
export type StatutIncident = 'OUVERT' | 'EN_COURS' | 'RESOLU';

/** Correspond à IncidentDto côté backend. */
export interface Incident {
  id: number;
  equipementId: number;
  equipementReference: string | null;
  stationId: number | null;
  stationNom: string | null;
  description: string | null;
  statut: StatutIncident;
  priorite: PrioriteIncident | null;
  prestataireId: number | null;
  prestataireNom: string | null;
  declarantPrenom: string | null;
  dateOuverture: string;
  dateResolution: string | null;
}

/** Correspond à DeclarationIncidentFormulaire côté backend. */
export interface DeclarationIncidentFormulaire {
  equipementId: number;
  description: string | null;
  priorite: PrioriteIncident;
}

/** Correspond à ModificationIncidentFormulaire côté backend. */
export interface ModificationIncidentFormulaire {
  description: string | null;
  priorite: PrioriteIncident;
}

@Injectable({ providedIn: 'root' })
export class IncidentService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/incidents';

  obtenirIncidentParId(id: number): Observable<Incident> {
    return this.http.get<Incident>(`${this.url}/${id}`);
  }

  modifierIncident(id: number, formulaire: ModificationIncidentFormulaire): Observable<Incident> {
    return this.http.put<Incident>(`${this.url}/${id}`, formulaire);
  }

  resoudreIncident(id: number): Observable<Incident> {
    return this.http.patch<Incident>(`${this.url}/${id}/resolution`, null);
  }

  supprimerIncident(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  obtenirTousIncidents(): Observable<Incident[]> {
    return this.http.get<Incident[]>(this.url);
  }

  declarerIncident(formulaire: DeclarationIncidentFormulaire): Observable<Incident> {
    return this.http.post<Incident>(this.url, formulaire);
  }

  affecterPrestataire(id: number, prestataireId: number): Observable<Incident> {
    return this.http.patch<Incident>(`${this.url}/${id}/prestataire`, { prestataireId });
  }

}
