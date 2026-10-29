import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {AuthService} from './auth.service';
import {catchError, map, of} from "rxjs";

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.utilisateurCourant()) {
    return true;
  }
  return authService.chargerUtilisateurCourant().pipe(
    map(() => true),
    catchError(() => of(router.createUrlTree(['/login'])))
  );
};
