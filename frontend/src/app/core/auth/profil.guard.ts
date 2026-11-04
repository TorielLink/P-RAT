import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';
import { CodeProfil, ROUTE_ACCUEIL_PAR_PROFIL } from './utilisateur-connecte';
import { catchError, map, of } from 'rxjs';

/**
 * Réserve une route à certains profils ; les autres sont renvoyés vers leur page d'accueil.
 * Confort d'affichage : la vraie protection est le 403 de Spring Security. À placer sous authGuard.
 */
export function profilGuard(...profils: CodeProfil[]): CanActivateFn {
  return () => {
    const profil = inject(AuthService).utilisateurCourant()?.profil;
    if (profil && profils.includes(profil)) {
      return true;
    }
    return inject(Router).createUrlTree([profil ? ROUTE_ACCUEIL_PAR_PROFIL[profil] : '/login']);
  };
}

/** Route vide « / » : envoie chaque profil vers sa page d'accueil. */
export const accueilGuard: CanActivateFn = () => {
  const profil = inject(AuthService).utilisateurCourant()?.profil;
  return inject(Router).createUrlTree([profil ? ROUTE_ACCUEIL_PAR_PROFIL[profil] : '/login']);
};

/** Page de connexion : un utilisateur déjà connecté (session encore valide) est renvoyé vers son accueil. */
export const dejaConnecteGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const versAccueil = (profil: CodeProfil) => router.createUrlTree([ROUTE_ACCUEIL_PAR_PROFIL[profil]]);

  const profil = authService.utilisateurCourant()?.profil;
  if (profil) {
    return versAccueil(profil);
  }
  // Rechargement de la page : on demande au serveur si la session existe encore
  return authService.chargerUtilisateurCourant().pipe(
    map(utilisateur => versAccueil(utilisateur.profil)),
    catchError(() => of(true))
  );
};
