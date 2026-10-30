import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export const PRIORITES_INCIDENT = ['BASSE', 'MOYENNE', 'HAUTE', 'CRITIQUE'] as const;
export type PrioriteIncident = (typeof PRIORITES_INCIDENT)[number];
export type StatutIncident = 'OUVERT' | 'EN_COURS' | 'RESOLU';
export const DESCRIPTION_MIN = 10;
export const DESCRIPTION_MAX = 500;
export type NiveauAttention = 'VERT' | 'ORANGE' | 'ROUGE' | 'PRIS_EN_CHARGE' | 'RESOLU';

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

/** Correspond à DeclarationIncidentRequest côté backend. */
export interface DeclarationIncidentRequest {
  description: string;
  priorite: PrioriteIncident;
}

/** Correspond à ModificationIncidentFormulaire côté backend. */
export interface ModificationIncidentFormulaire {
  description: string | null;
  priorite: PrioriteIncident;
}

export interface EvaluationIncident {
  note: number;
  commentaire: string | null;
}

/** Correspond à IncidentDetailDto côté backend. */
export interface IncidentDetail extends Incident {
  dureeOuvertureMinutes: number;
  niveauAttention: NiveauAttention;
  evaluation: EvaluationIncident | null;
}

export const LIBELLES_STATUT: Record<StatutIncident, string> = {
  OUVERT: 'Non affecté',
  EN_COURS: 'Pris en charge',
  RESOLU: 'Résolu',
};

export const LIBELLES_NIVEAU: Record<NiveauAttention, string> = {
  VERT: 'Vert (moins de 48 h)',
  ORANGE: 'Orange (48 h à 72 h)',
  ROUGE: 'Rouge (72 h et plus)',
  PRIS_EN_CHARGE: 'Pris en charge',
  RESOLU: 'Résolu',
};

@Injectable({ providedIn: 'root' })
export class IncidentService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/incidents';

  obtenirIncidentParId(id: number): Observable<IncidentDetail> {
    return this.http.get<IncidentDetail>(`${this.url}/${id}`);
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

  declarerIncident(equipementId: number, demande: DeclarationIncidentRequest): Observable<Incident> {
    return this.http.post<Incident>(`/api/equipements/${equipementId}/incidents`, demande);
  }

  affecterPrestataire(id: number, prestataireId: number): Observable<Incident> {
    return this.http.patch<Incident>(`${this.url}/${id}/prestataire`, { prestataireId });
  }

}
