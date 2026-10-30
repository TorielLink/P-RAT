import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { Equipement, EquipementService } from '../../core/equipement/equipement.service';
import { IncidentService, PRIORITES_INCIDENT, PrioriteIncident } from '../../core/incident/incident.service';

/** Page /incidents/nouveau (?equipement=ID pour présélectionner l'équipement). */
@Component({
  selector: 'app-nouvel-incident',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './nouvel_incident.html',
})
export class NouvelIncident implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly incidentService = inject(IncidentService);
  private readonly equipementService = inject(EquipementService);
  protected readonly authService = inject(AuthService);

  protected readonly priorites = PRIORITES_INCIDENT;
  readonly equipements = signal<Equipement[]>([]);
  readonly erreur = signal<string | null>(null);

  readonly formulaire = inject(FormBuilder).group({
    equipementId: [null as number | null, Validators.required],
    priorite: ['MOYENNE' as PrioriteIncident, Validators.required],
    description: ['', Validators.maxLength(2000)],
  });

  ngOnInit(): void {
    this.equipementService.obtenirTousEquipements().subscribe({
      next: equipements => this.equipements.set(equipements),
      error: (erreur: HttpErrorResponse) =>
        this.erreur.set(erreur.error?.message ?? 'Impossible de charger les équipements.'),
    });

    const equipementId = this.route.snapshot.queryParamMap.get('equipement');
    if (equipementId) {
      this.formulaire.controls.equipementId.setValue(Number(equipementId));
    }
  }

  declarer(): void {
    if (this.formulaire.invalid) {
      this.erreur.set("Choisissez l'équipement concerné.");
      return;
    }
    const valeurs = this.formulaire.getRawValue();
    this.incidentService
      .declarerIncident({
        equipementId: valeurs.equipementId!,
        priorite: valeurs.priorite!,
        description: valeurs.description?.trim() || null,
      })
      .subscribe({
        next: incident => this.router.navigate(['/incidents', incident.id]),
        error: (erreur: HttpErrorResponse) =>
          this.erreur.set(
            erreur.status === 403
              ? 'Votre profil ne permet pas de déclarer un incident.'
              : (erreur.error?.detail ?? erreur.error?.message ?? 'Échec de la déclaration.')
          ),
      });
  }
}
