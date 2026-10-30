import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Station {
  id: number;
  nom: string;
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

  ajouterStation(nom: string): Observable<Station> {
    return this.http.post<Station>(this.url, { nom });
  }

  modifierStation(id: number, nom: string): Observable<Station> {
    return this.http.put<Station>(`${this.url}/${id}`, { nom });
  }

  supprimerStation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
