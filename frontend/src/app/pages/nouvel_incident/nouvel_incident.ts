import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { Equipement, EquipementService } from '../../core/equipement/equipement.service';
import {
  DESCRIPTION_MAX,
  DESCRIPTION_MIN,
  IncidentService,
  PRIORITES_INCIDENT,
  PrioriteIncident,
} from '../../core/incident/incident.service';
import { LibellePipe } from '../../shared/libelle.pipe';


/** Page /equipements/:id/declarer-incident */
@Component({
  selector: 'app-nouvel-incident',
  imports: [ReactiveFormsModule, RouterLink, LibellePipe],
  templateUrl: './nouvel_incident.html',
})
export class NouvelIncident implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly incidentService = inject(IncidentService);
  private readonly equipementService = inject(EquipementService);
  protected readonly authService = inject(AuthService);

  protected readonly priorites = PRIORITES_INCIDENT;
  protected readonly descriptionMin = DESCRIPTION_MIN;
  protected readonly descriptionMax = DESCRIPTION_MAX;

  readonly equipement = signal<Equipement | null>(null);
  readonly erreur = signal<string | null>(null);
  readonly erreursServeur = signal<Record<string, string>>({});

  readonly formulaire = inject(FormBuilder).nonNullable.group({
    description: ['', [Validators.required, Validators.minLength(DESCRIPTION_MIN), Validators.maxLength(DESCRIPTION_MAX)]],
    priorite: ['MOYENNE' as PrioriteIncident, Validators.required],
  });

  ngOnInit(): void {
    const equipementId = Number(this.route.snapshot.paramMap.get('id'));
    this.equipementService.obtenirEquipementParId(equipementId).subscribe({
      next: equipement => this.equipement.set(equipement),
      error: (erreur: HttpErrorResponse) => this.erreur.set(erreur.error?.detail ?? 'Équipement introuvable.'),
    });
  }

  declarer(equipement: Equipement): void {
    this.erreursServeur.set({});
    if (this.formulaire.invalid) {
      this.formulaire.markAllAsTouched();
      return;
    }
    const { description, priorite } = this.formulaire.getRawValue();
    this.incidentService.declarerIncident(equipement.id, { description: description.trim(), priorite }).subscribe({
      next: incident => this.router.navigate(['/incidents', incident.id]),
      error: (erreur: HttpErrorResponse) => this.afficherErreur(erreur),
    });
  }

  private afficherErreur(erreur: HttpErrorResponse): void {
    if (erreur.status === HttpStatusCode.BadRequest && erreur.error?.erreurs) {
      this.erreursServeur.set(erreur.error.erreurs);
      return;
    }
    this.erreur.set(
      erreur.status === HttpStatusCode.Forbidden
        ? 'Votre profil ne permet pas de déclarer un incident.'
        : (erreur.error?.detail ?? 'Échec de la déclaration.')
    );
  }
}
