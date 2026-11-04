import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Incident, IncidentService, StatutIncident } from '../../core/incident/incident.service';
import { AuthService } from '../../core/auth/auth.service';
import { LibellePipe } from '../../shared/libelle.pipe';


/** Page /incidents : ouverts à gauche, en cours au centre, résolus à droite. */
@Component({
  selector: 'app-incidents',
  imports: [DatePipe, RouterLink, LibellePipe],
  templateUrl: './incidents.html',
  styleUrl: './incidents.scss',
})
export class Incidents implements OnInit {
  private readonly incidentService = inject(IncidentService);
  protected readonly authService = inject(AuthService);
  readonly incidents = signal<Incident[]>([]);
  readonly erreur = signal<string | null>(null);


  private static readonly LIMITE_PAR_COLONNE = 5;

  // Recalculé automatiquement à chaque changement de la liste
  readonly colonnes = computed(() => [
    this.colonne('Non affectés', 'OUVERT', this.parStatut('OUVERT'), '/incidents/non-affectes'),
    this.colonne('Pris en charge', 'EN_COURS', this.parStatut('EN_COURS'), '/incidents/en-cours'),
    // Comme l'historique : les plus récemment résolus d'abord
    this.colonne('Résolus', 'RESOLU', this.parStatut('RESOLU').sort(parDateResolutionDecroissante), '/incidents/historique'),
  ]);

  // statut : sert à la couleur de la colonne (classe CSS)
  private colonne(titre: string, statut: StatutIncident, incidents: Incident[], lien: string | null) {
    return {
      titre,
      statut,
      lien,
      total: incidents.length,
      incidents: incidents.slice(0, Incidents.LIMITE_PAR_COLONNE),
    };
  }

  ngOnInit(): void {
    this.incidentService.obtenirTousIncidents().subscribe({
      next: incidents => this.incidents.set(incidents),
      error: (erreur: HttpErrorResponse) =>
        this.erreur.set(erreur.error?.message ?? 'Impossible de charger les incidents.'),
    });
  }

  private parStatut(statut: StatutIncident): Incident[] {
    return this.incidents().filter(incident => incident.statut === statut);
  }


}

function parDateResolutionDecroissante(a: Incident, b: Incident): number {
  return (b.dateResolution ?? '').localeCompare(a.dateResolution ?? '');
}
