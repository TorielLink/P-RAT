import {Component, inject, signal} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../core/auth/auth.service';
import {HttpErrorResponse, HttpStatusCode} from '@angular/common/http';
import {ActivatedRoute, Router} from '@angular/router';
import {CodeProfil, ROUTE_ACCUEIL_PAR_PROFIL} from '../../core/auth/utilisateur-connecte';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class LoginComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  readonly formulaire = inject(FormBuilder).nonNullable.group({
    login: ['', Validators.required],
    motDePasse: ['', Validators.required],
  });
  readonly messageErreur = signal<string | null>(null);

  seConnecter(): void {
    if (this.formulaire.invalid) {
      this.formulaire.markAllAsTouched();
      return;
    }
    const { login, motDePasse } = this.formulaire.getRawValue();
    this.authService.connecter(login, motDePasse).subscribe({
      next: utilisateur => this.router.navigateByUrl(this.destination(utilisateur.profil)),
      error: (erreur: HttpErrorResponse) => this.afficherErreur(erreur),
    });
  }

  private afficherErreur(erreur: HttpErrorResponse): void {
    this.messageErreur.set(
      erreur.status === HttpStatusCode.Unauthorized
        ? 'Identifiant ou mot de passe incorrect.'
        : 'Le service est momentanément indisponible. Réessayez plus tard.'
    );
    this.formulaire.controls.motDePasse.reset();
  }

  /** Page demandée avant la connexion */
  private destination(profil: CodeProfil): string {
    const retour = this.route.snapshot.queryParamMap.get('retour');
    // Uniquement un chemin interne : pas de redirection vers un autre site
    return retour && retour.startsWith('/') && !retour.startsWith('//') ? retour : ROUTE_ACCUEIL_PAR_PROFIL[profil];
  }
}
