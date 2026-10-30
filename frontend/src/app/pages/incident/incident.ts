import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import {
  Incident,
  IncidentService,
  PRIORITES_INCIDENT,
  PrioriteIncident,
} from '../../core/incident/incident.service';

/** Page /incidents/:id : un incident seul. */
@Component({
  selector: 'app-incident',
  imports: [DatePipe, ReactiveFormsModule, RouterLink],
  templateUrl: './incident.html',
})
export class IncidentPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly incidentService = inject(IncidentService);
  protected readonly authService = inject(AuthService);

  protected readonly priorites = PRIORITES_INCIDENT;
  readonly incident = signal<Incident | null>(null);
  readonly enModification = signal(false);
  readonly erreur = signal<string | null>(null);

  readonly formulaire = inject(FormBuilder).nonNullable.group({
    description: ['', Validators.maxLength(2000)],
    priorite: ['MOYENNE' as PrioriteIncident, Validators.required],
  });

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.erreur.set(null);
      this.enModification.set(false);
      this.incidentService.obtenirIncidentParId(Number(params.get('id'))).subscribe({
        next: incident => this.incident.set(incident),
        error: (erreur: HttpErrorResponse) => {
          this.incident.set(null);
          this.erreur.set(this.message(erreur, 'Incident introuvable.'));
        },
      });
    });
  }

  modifier(incident: Incident): void {
    this.formulaire.setValue({
      description: incident.description ?? '',
      priorite: incident.priorite ?? 'MOYENNE',
    });
    this.erreur.set(null);
    this.enModification.set(true);
  }

  annuler(): void {
    this.enModification.set(false);
    this.erreur.set(null);
  }

  enregistrer(incident: Incident): void {
    const valeurs = this.formulaire.getRawValue();
    this.incidentService
      .modifierIncident(incident.id, {
        description: valeurs.description.trim() || null,
        priorite: valeurs.priorite,
      })
      .subscribe({
        next: modifie => {
          this.incident.set(modifie);
          this.enModification.set(false);
          this.erreur.set(null);
        },
        error: (erreur: HttpErrorResponse) => this.erreur.set(this.message(erreur, 'Échec de la modification.')),
      });
  }

  resoudre(incident: Incident): void {
    if (!confirm(`Marquer l'incident n°${incident.id} comme résolu ? Il ne pourra plus être modifié.`)) {
      return;
    }
    this.incidentService.resoudreIncident(incident.id).subscribe({
      next: resolu => this.incident.set(resolu),
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

  private message(erreur: HttpErrorResponse, parDefaut: string): string {
    if (erreur.status === 403) {
      return 'Votre profil ne permet pas cette action.';
    }
    return erreur.error?.detail ?? erreur.error?.message ?? parDefaut;
  }

}
