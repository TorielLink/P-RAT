import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import {
  Incident,
  IncidentDetail,
  IncidentService,
  LIBELLES_NIVEAU,
  LIBELLES_STATUT,
  PRIORITES_INCIDENT,
  PrioriteIncident,
} from '../../core/incident/incident.service';
import { formaterDuree } from '../../shared/formater-duree';

/** Page /incidents/:id : détail d'un incident. */
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
  protected readonly libellesStatut = LIBELLES_STATUT;
  protected readonly libellesNiveau = LIBELLES_NIVEAU;
  protected readonly formaterDuree = formaterDuree;

  readonly incident = signal<IncidentDetail | null>(null);
  readonly enModification = signal(false);
  readonly erreur = signal<string | null>(null);

  readonly formulaire = inject(FormBuilder).nonNullable.group({
    description: ['', Validators.maxLength(2000)],
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
        next: () => this.charger(incident.id),
        error: (erreur: HttpErrorResponse) => this.erreur.set(this.message(erreur, 'Échec de la modification.')),
      });
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
    if (erreur.status === 403) {
      return 'Votre profil ne permet pas cette action.';
    }
    return erreur.error?.detail ?? erreur.error?.message ?? parDefaut;
  }
}
