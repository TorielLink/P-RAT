import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

/** Correspond à PrestataireResumeDto côté backend. */
export interface Prestataire {
  id: number;
  nom: string;
  email: string | null;
  telephone: string | null;
  noteMoyenne: number | null;
  nombreEvaluations: number;
}

/** Correspond à EvaluationPrestataireDto côté backend. */
export interface EvaluationPrestataire {
  incidentId: number;
  equipementReference: string | null;
  stationNom: string | null;
  note: number;
  commentaire: string | null;
  dateEvaluation: string;
}

/** Correspond à PrestataireFicheDto côté backend. */
export interface FichePrestataire extends Prestataire {
  evaluations: EvaluationPrestataire[];
}

@Injectable({ providedIn: 'root' })
export class PrestataireService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/prestataires';

  obtenirTousPrestataires(): Observable<Prestataire[]> {
    return this.http.get<Prestataire[]>(this.url);
  }

  obtenirFiche(id: number): Observable<FichePrestataire> {
    return this.http.get<FichePrestataire>(`${this.url}/${id}`);
  }
}
