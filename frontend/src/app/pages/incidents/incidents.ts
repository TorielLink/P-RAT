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


  readonly colonnes = computed(() => [
    { titre: 'Ouverts', incidents: this.parStatut('OUVERT'), lien: null },
    { titre: 'En cours', incidents: this.parStatut('EN_COURS'), lien: null },
    { titre: 'Résolus', incidents: this.parStatut('RESOLU'), lien: '/incidents/historique' },
  ]);

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
