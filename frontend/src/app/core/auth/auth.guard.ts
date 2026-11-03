import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {AuthService} from './auth.service';
import {catchError, map, of} from "rxjs";

export const authGuard: CanActivateFn = (_route, etat) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.utilisateurCourant()) {
    return true;
  }
  return authService.chargerUtilisateurCourant().pipe(
    map(() => true),
    // Mémorise la page demandée : après connexion, l'utilisateur y revient
    catchError(() => of(router.createUrlTree(['/login'], { queryParams: { retour: etat.url } })))
  );
};
