import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  return authService.estAdmin() ? true : inject(Router).createUrlTree(['/incidents']);
};

export const tableauDeBordGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  return authService.peutVoirTableauDeBord() ? true : inject(Router).createUrlTree(['/incidents']);
};
