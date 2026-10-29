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
}
