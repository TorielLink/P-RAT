import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TypeEquipement } from '../equipement/equipement.service';

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

/** Correspond à ModificationIncidentRequest côté backend. */
export interface ModificationIncidentRequest {
  description: string;
  priorite: PrioriteIncident;
}

export interface EvaluationIncident {
  note: number;
  commentaire: string | null;
  dateEvaluation: string;
}

export const NOTE_MIN = 1;
export const NOTE_MAX = 5;
export const COMMENTAIRE_MAX = 500;
export const NOTES_POSSIBLES = Array.from({ length: NOTE_MAX - NOTE_MIN + 1 }, (_, rang) => NOTE_MIN + rang);

/** Correspond à EvaluationRequest côté backend. */
export interface EvaluationRequest {
  note: number;
  commentaire: string | null;
}

/** Correspond à IncidentDetailDto côté backend. */
export interface IncidentDetail extends Incident {
  dureeOuvertureMinutes: number;
  niveauAttention: NiveauAttention;
  evaluation: EvaluationIncident | null;
  modifiable: boolean;
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

/** Une ligne de l'historique : correspond à HistoriqueIncidentDto côté backend. */
export interface HistoriqueIncident {
  id: number;
  equipementId: number;
  equipementReference: string | null;
  stationId: number | null;
  stationNom: string | null;
  description: string | null;
  dateOuverture: string;
  dateResolution: string;
  dureeTraitementMinutes: number;
  prestataireNom: string | null;
  note: number | null;
}

/** Une page de résultats : correspond à PageDto côté backend. */
export interface Page<T> {
  contenu: T[];
  page: number;
  taille: number;
  totalElements: number;
  totalPages: number;
}

/** Correspond à IncidentActifDto côté backend */
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

export type TypeEvenementIncident =
  'DECLARATION' | 'MODIFICATION' | 'AFFECTATION' | 'REAFFECTATION' | 'RESOLUTION' | 'EVALUATION';

export interface EvenementIncident {
  dateEvenement: string;
  auteur: string | null;
  type: TypeEvenementIncident;
  champ: string | null;
  ancienneValeur: string | null;
  nouvelleValeur: string | null;
}

export const LIBELLES_EVENEMENT: Record<TypeEvenementIncident, string> = {
  DECLARATION: 'Déclaration',
  MODIFICATION: 'Modification',
  AFFECTATION: 'Affectation',
  REAFFECTATION: 'Réaffectation',
  RESOLUTION: 'Résolution',
  EVALUATION: 'Évaluation',
};

// Noms des champs tels qu'écrits par le backend
export const LIBELLES_CHAMP: Record<string, string> = {
  description: 'Description',
  priorite: 'Priorité',
  prestataire: 'Prestataire',
  statut: 'Statut',
  note: 'Note',
};

@Injectable({ providedIn: 'root' })
export class IncidentService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/incidents';

  obtenirIncidentParId(id: number): Observable<IncidentDetail> {
    return this.http.get<IncidentDetail>(`${this.url}/${id}`);
  }

  modifierIncident(id: number, demande: ModificationIncidentRequest): Observable<Incident> {
    return this.http.patch<Incident>(`${this.url}/${id}`, demande);
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
    return this.http.put<Incident>(`${this.url}/${id}/prestataire`, { prestataireId });
  }

  obtenirIncidentsParEquipement(equipementId: number): Observable<Incident[]> {
    return this.http.get<Incident[]>(this.url, { params: new HttpParams().set('equipementId', equipementId) });
  }

  obtenirHistorique(page: number, taille = 20): Observable<Page<HistoriqueIncident>> {
    const params = new HttpParams().set('page', page).set('taille', taille);
    return this.http.get<Page<HistoriqueIncident>>(`${this.url}/historique`, { params });
  }

  obtenirIncidentsActifs(): Observable<IncidentActif[]> {
    return this.http.get<IncidentActif[]>(`${this.url}/actifs`);
  }

  evaluerIncident(id: number, demande: EvaluationRequest): Observable<EvaluationIncident> {
    return this.http.post<EvaluationIncident>(`${this.url}/${id}/evaluation`, demande);
  }

  obtenirJournal(id: number): Observable<EvenementIncident[]> {
    return this.http.get<EvenementIncident[]>(`${this.url}/${id}/journal`);
  }

}
