import {HttpErrorResponse, HttpInterceptorFn, HttpStatusCode} from '@angular/common/http';
import { inject } from "@angular/core";
import { AuthService } from "./auth.service";
import {Router} from '@angular/router';
import {catchError, throwError} from 'rxjs';

const URLS_SANS_REDIRECTION = ['/api/auth/login', '/api/auth/me'];

export const authInterceptor: HttpInterceptorFn = (requete, suivant) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return suivant(requete).pipe(
    catchError((erreur: HttpErrorResponse) => {
      if (erreur.status === HttpStatusCode.Unauthorized && !URLS_SANS_REDIRECTION.includes(requete.url)) {
        authService.oublierUtilisateur();
        // Session expirée : après reconnexion, l'utilisateur revient sur la page où il était
        const retour = router.url.startsWith('/login') ? undefined : router.url;
        router.navigate(['/login'], { queryParams: retour ? { retour } : {} });
      }
      return throwError(() => erreur);
    })
  );
};
