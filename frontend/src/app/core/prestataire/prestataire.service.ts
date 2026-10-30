import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Prestataire {
  id: number;
  nom: string;
  email: string | null;
  telephone: string | null;
}

@Injectable({ providedIn: 'root' })
export class PrestataireService {
  private readonly http = inject(HttpClient);

  obtenirTousPrestataires(): Observable<Prestataire[]> {
    return this.http.get<Prestataire[]>('/api/prestataires');
  }
}
