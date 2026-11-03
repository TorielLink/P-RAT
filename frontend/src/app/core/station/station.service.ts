import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Station {
  id: number;
  nom: string;
  /** Position sur la carte en grille (-20 à 20), null si la station n'est pas placée. */
  positionX: number | null;
  positionY: number | null;
}

/** Correspond à StationFormulaire côté backend. */
export interface StationFormulaire {
  nom: string;
  positionX: number | null;
  positionY: number | null;
}

/** Situation d'une station sur la carte : pire niveau parmi ses incidents actifs. */
export type SituationStation = 'ROUGE' | 'ORANGE' | 'VERT' | 'PRIS_EN_CHARGE' | 'AUCUN';

/** Correspond à StationCarteDto côté backend. */
export interface StationCarte {
  id: number;
  nom: string;
  positionX: number | null;
  positionY: number | null;
  nombreEquipements: number;
  situation: SituationStation;
  rouge: number;
  orange: number;
  vert: number;
  prisEnCharge: number;
}

@Injectable({ providedIn: 'root' })
export class StationService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/stations';

  obtenirStationParId(id: number): Observable<Station> {
    return this.http.get<Station>(`${this.url}/${id}`);
  }

  obtenirToutesStations(): Observable<Station[]> {
    return this.http.get<Station[]>(this.url);
  }

  ajouterStation(formulaire: StationFormulaire): Observable<Station> {
    return this.http.post<Station>(this.url, formulaire);
  }

  modifierStation(id: number, formulaire: StationFormulaire): Observable<Station> {
    return this.http.put<Station>(`${this.url}/${id}`, formulaire);
  }

  supprimerStation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
  
  obtenirCarte(): Observable<StationCarte[]> {
    return this.http.get<StationCarte[]>(`${this.url}/carte`);
  }
}
