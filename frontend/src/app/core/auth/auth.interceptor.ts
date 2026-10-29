import {HttpErrorResponse, HttpInterceptorFn, HttpStatusCode} from '@angular/common/http';
import { inject } from "@angular/core";
import { AuthService } from "./auth.service";
import {Router} from '@angular/router';
import {catchError, throwError} from 'rxjs';

const URL_CONNEXION = '/api/auth/login';

export const authInterceptor: HttpInterceptorFn = (requete, suivant) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return suivant(requete).pipe(
    catchError((erreur: HttpErrorResponse) => {
      if (erreur.status === HttpStatusCode.Unauthorized && requete.url !== URL_CONNEXION) {
        authService.oublierUtilisateur();
        router.navigate(['/login']);
      }
      return throwError(() => erreur);
    })
  );
};
