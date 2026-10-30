import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';

export const ETATS_EQUIPEMENT = ['EN_SERVICE', 'EN_PANNE', 'EN_MAINTENANCE', 'HORS_SERVICE'] as const;
export type EtatEquipement = (typeof ETATS_EQUIPEMENT)[number];
export const TYPES_EQUIPEMENT = ['CAMERA', 'ASCENSEUR', 'PORTE', 'ESCALATOR'] as const;
export type TypeEquipement = (typeof TYPES_EQUIPEMENT)[number];

/** Equivalent EquipementDto*/
export interface Equipement {
  id: number;
  reference: string | null;
  description: string | null;
  type: TypeEquipement;
  etat: EtatEquipement;
  stationId: number | null;
  stationNom: string | null;
  dateCreation: string;
}

export interface EquipementFormulaire {
  reference: string | null;
  description: string | null;
  type: TypeEquipement;
  etat: EtatEquipement;
  stationId: number | null;
}

@Injectable({ providedIn: 'root' })
export class EquipementService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/equipements';

  obtenirEquipementParId(id: number): Observable<Equipement> {
    return this.http.get<Equipement>(`${this.url}/${id}`);
  }

  changerEtatEquipement(id: number, etat: EtatEquipement): Observable<Equipement> {
    return this.http.patch<Equipement>(`${this.url}/${id}/etat`, { etat });
  }

  supprimerEquipement(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  modifierEquipement(id: number, formulaire: EquipementFormulaire): Observable<Equipement> {
    return this.http.put<Equipement>(`${this.url}/${id}`, formulaire);
  }

  obtenirEquipementsParStation(stationId: number): Observable<Equipement[]> {
    return this.http.get<Equipement[]>(this.url, { params: new HttpParams().set('stationId', stationId) });
  }

  ajouterEquipement(formulaire: EquipementFormulaire): Observable<Equipement> {
    return this.http.post<Equipement>(this.url, formulaire);
  }
  
  obtenirTousEquipements(): Observable<Equipement[]> {
    return this.http.get<Equipement[]>(this.url);
  }
}
