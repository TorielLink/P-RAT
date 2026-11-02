import {inject, Injectable, signal} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {UtilisateurConnecte} from './utilisateur-connecte';
import {finalize, Observable, switchMap, tap} from "rxjs";
import {computed} from "@angular/core"

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  readonly utilisateurCourant = signal<UtilisateurConnecte | null>(null);

  connecter(login: string, motDePasse: string): Observable<UtilisateurConnecte> {
    const corps = new HttpParams().set('login', login).set('motDePasse', motDePasse);
    return this.http.post<void>('/api/auth/login', corps).pipe(
      switchMap(() => this.chargerUtilisateurCourant())
    );
  }

  chargerUtilisateurCourant(): Observable<UtilisateurConnecte> {
    return this.http.get<UtilisateurConnecte>('/api/auth/me').pipe(
      tap(utilisateur => this.utilisateurCourant.set(utilisateur))
    );
  }

  deconnecter(): Observable<void> {
    return this.http.post<void>('/api/auth/logout', null).pipe(
      finalize(() => this.oublierUtilisateur())
    );
  }

  oublierUtilisateur(): void {
    this.utilisateurCourant.set(null);
  }
  readonly peutGererEquipements = computed(() =>
    ['ADMIN', 'AGENT'].includes(this.utilisateurCourant()?.profil ?? '')
  );
  readonly peutGererStations = computed(() => this.utilisateurCourant()?.profil === 'ADMIN');
  readonly estAdmin = computed(() => this.utilisateurCourant()?.profil === 'ADMIN');
  readonly peutDeclarerIncidents = computed(() =>
    ['ADMIN', 'AGENT'].includes(this.utilisateurCourant()?.profil ?? '')
  );
}
