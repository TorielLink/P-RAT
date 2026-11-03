import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { Incident, IncidentService } from '../../core/incident/incident.service';
import { Prestataire, PrestataireService } from '../../core/prestataire/prestataire.service';
import { LibellePipe } from '../../shared/libelle.pipe';

/** Page /affectations : affecter ou réaffecter un prestataire aux incidents non résolus (admin). */
@Component({
  selector: 'app-affectations',
  imports: [RouterLink, LibellePipe],
  templateUrl: './affectations.html',
})
export class Affectations implements OnInit {
  private readonly incidentService = inject(IncidentService);
  private readonly prestataireService = inject(PrestataireService);
  protected readonly authService = inject(AuthService);

  readonly incidents = signal<Incident[]>([]);
  readonly prestataires = signal<Prestataire[]>([]);
  readonly message = signal<string | null>(null);

  readonly incidentsNonResolus = computed(() =>
    this.incidents().filter(incident => incident.statut !== 'RESOLU')
  );

  ngOnInit(): void {
    if (!this.authService.estAdmin()) {
      return;
    }
    this.incidentService.obtenirTousIncidents().subscribe({
      next: incidents => this.incidents.set(incidents),
      error: (erreur: HttpErrorResponse) => this.message.set(this.texteErreur(erreur, 'Impossible de charger les incidents.')),
    });
    this.prestataireService.obtenirTousPrestataires().subscribe({
      next: prestataires => this.prestataires.set(prestataires),
      error: (erreur: HttpErrorResponse) =>
        this.message.set(this.texteErreur(erreur, 'Impossible de charger les prestataires.')),
    });
  }

  affecter(incident: Incident, prestataireId: string): void {
    if (!prestataireId) {
      this.message.set(`Choisissez un prestataire pour l'incident n°${incident.id}.`);
      return;
    }
    this.incidentService.affecterPrestataire(incident.id, Number(prestataireId)).subscribe({
      next: modifie => {
        // Remplace la ligne : statut EN_COURS et nouveau prestataire affichés sans recharger
        this.incidents.update(liste => liste.map(i => (i.id === modifie.id ? modifie : i)));
        this.message.set(`Incident n°${modifie.id} affecté à ${modifie.prestataireNom}.`);
      },
      error: (erreur: HttpErrorResponse) => this.message.set(this.texteErreur(erreur, "Échec de l'affectation.")),
    });
  }

  private texteErreur(erreur: HttpErrorResponse, parDefaut: string): string {
    if (erreur.status === 403) {
      return 'Votre profil ne permet pas cette action.';
    }
    return erreur.error?.detail ?? erreur.error?.message ?? parDefaut;
  }
}
