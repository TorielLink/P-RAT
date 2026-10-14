import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Equipement {
  id: number;
  reference: string;
  libelle: string;
  description: string | null;
  type: string;
  station: string;
  etat: string;
  dateCreation: string;
}

@Injectable({ providedIn: 'root' })
export class EquipementService {
  private readonly http = inject(HttpClient);

  // URL relative : en dev, le proxy Angular (proxy.conf.json) redirige /api vers http://localhost:8080
  getAll(): Observable<Equipement[]> {
    return this.http.get<Equipement[]>('/api/equipements');
  }

  add(equipement: Equipement) {
    return this.http.post<Equipement>('/api/equipements', equipement);
  }
}
