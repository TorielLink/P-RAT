import {Component, OnInit, inject, signal} from '@angular/core';
import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import {DatePipe} from '@angular/common';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {AuthService} from '../../core/auth/auth.service';
import {
  DESCRIPTION_MAX,
  DESCRIPTION_MIN,
  Incident,
  IncidentDetail,
  IncidentService,
  LIBELLES_NIVEAU,
  LIBELLES_STATUT,
  PRIORITES_INCIDENT,
  PrioriteIncident,
} from '../../core/incident/incident.service';
import {formaterDuree} from '../../shared/formater-duree';
import { LibellePipe } from '../../shared/libelle.pipe';
import { NiveauAttentionComponent } from '../../shared/niveau-attention';
import { StatutIncidentComponent } from '../../shared/statut-incident';

/** Page /incidents/:id : détail d'un incident. */
@Component({
  selector: 'app-incident',
  imports: [DatePipe, ReactiveFormsModule, RouterLink, LibellePipe, NiveauAttentionComponent, StatutIncidentComponent],
  templateUrl: './incident.html',
})
export class IncidentPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly incidentService = inject(IncidentService);
  protected readonly authService = inject(AuthService);

  protected readonly priorites = PRIORITES_INCIDENT;
  protected readonly formaterDuree = formaterDuree;

  protected readonly descriptionMin = DESCRIPTION_MIN;
  protected readonly descriptionMax = DESCRIPTION_MAX;
  readonly erreursServeur = signal<Record<string, string>>({});

  readonly incident = signal<IncidentDetail | null>(null);
  readonly enModification = signal(false);
  readonly erreur = signal<string | null>(null);

  readonly formulaire = inject(FormBuilder).nonNullable.group({
    description: ['', [Validators.required, Validators.minLength(DESCRIPTION_MIN), Validators.maxLength(DESCRIPTION_MAX)]],
    priorite: ['MOYENNE' as PrioriteIncident, Validators.required],
  });

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => this.charger(Number(params.get('id'))));
  }

  modifier(incident: Incident): void {
    this.formulaire.setValue({
      description: incident.description ?? '',
      priorite: incident.priorite ?? 'MOYENNE',
    });
    this.erreur.set(null);
    this.erreursServeur.set({});
    this.enModification.set(true);
  }

  annuler(): void {
    this.enModification.set(false);
    this.erreur.set(null);
  }

  enregistrer(incident: Incident): void {
    this.erreursServeur.set({});
    if (this.formulaire.invalid) {
      this.formulaire.markAllAsTouched();
      return;
    }
    const {description, priorite} = this.formulaire.getRawValue();
    this.incidentService.modifierIncident(incident.id, {description: description.trim(), priorite}).subscribe({
      next: () => this.charger(incident.id),
      error: (erreur: HttpErrorResponse) => this.afficherErreurModification(erreur),
    });
  }

  private afficherErreurModification(erreur: HttpErrorResponse): void {
    if (erreur.status === HttpStatusCode.BadRequest && erreur.error?.erreurs) {
      this.erreursServeur.set(erreur.error.erreurs);
      return;
    }
    this.erreur.set(this.message(erreur, 'Échec de la modification.'));
  }

  resoudre(incident: Incident): void {
    if (!confirm(`Marquer l'incident n°${incident.id} comme résolu ? Il ne pourra plus être modifié.`)) {
      return;
    }
    this.incidentService.resoudreIncident(incident.id).subscribe({
      next: () => this.charger(incident.id),
      error: (erreur: HttpErrorResponse) => this.erreur.set(this.message(erreur, 'Échec de la résolution.')),
    });
  }

  supprimer(incident: Incident): void {
    if (!confirm(`Supprimer l'incident n°${incident.id} et son évaluation ?`)) {
      return;
    }
    this.incidentService.supprimerIncident(incident.id).subscribe({
      next: () => this.router.navigate(['/incidents']),
      error: (erreur: HttpErrorResponse) => this.erreur.set(this.message(erreur, 'Échec de la suppression.')),
    });
  }

  // Après une modification ou une résolution, on recharge le détail :
  // seul GET /api/incidents/{id} renvoie la durée, le niveau d'attention et l'évaluation à jour.
  private charger(id: number): void {
    this.erreur.set(null);
    this.enModification.set(false);
    this.incidentService.obtenirIncidentParId(id).subscribe({
      next: incident => this.incident.set(incident),
      error: (erreur: HttpErrorResponse) => {
        this.incident.set(null);
        this.erreur.set(this.message(erreur, 'Incident introuvable.'));
      },
    });
  }

  private message(erreur: HttpErrorResponse, parDefaut: string): string {
    if (erreur.error?.detail) {
      return erreur.error.detail;
    }
    return erreur.status === HttpStatusCode.Forbidden ? 'Votre profil ne permet pas cette action.' : parDefaut;
  }
}
