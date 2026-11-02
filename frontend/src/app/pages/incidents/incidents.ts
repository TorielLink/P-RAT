import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Incident, IncidentService, StatutIncident } from '../../core/incident/incident.service';
import { AuthService } from '../../core/auth/auth.service';



/** Page /incidents : ouverts à gauche, en cours au centre, résolus à droite. */
@Component({
  selector: 'app-incidents',
  imports: [DatePipe, RouterLink],
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
    this.colonne('Ouverts', this.parStatut('OUVERT'), null),
    this.colonne('En cours', this.parStatut('EN_COURS'), null),
    // Comme l'historique : les plus récemment résolus d'abord
    this.colonne('Résolus', this.parStatut('RESOLU').sort(parDateResolutionDecroissante), '/incidents/historique'),
  ]);

  private colonne(titre: string, incidents: Incident[], lien: string | null) {
    return {
      titre,
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
