import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Incident, IncidentService, StatutIncident } from '../../core/incident/incident.service';
import { LibellePipe } from '../../shared/libelle.pipe';

/**
 * Pages /incidents/non-affectes et /incidents/en-cours : tous les incidents d'un statut.
 * Le statut et le titre viennent de la route (data) ; le serveur filtre selon le profil.
 */
@Component({
  selector: 'app-incidents-statut',
  imports: [DatePipe, RouterLink, LibellePipe],
  templateUrl: './incidents-statut.html',
  // Mêmes cartes que la page /incidents
  styleUrls: ['../incidents/incidents.scss', './incidents-statut.scss'],
})
export class IncidentsParStatut implements OnInit {
  private readonly incidentService = inject(IncidentService);
  private readonly route = inject(ActivatedRoute);

  readonly statut: StatutIncident = this.route.snapshot.data['statut'];
  readonly titre: string = this.route.snapshot.data['titre'];

  readonly incidents = signal<Incident[]>([]);
  readonly charge = signal(false);
  readonly erreur = signal<string | null>(null);

  ngOnInit(): void {
    this.incidentService.obtenirIncidentsParStatut(this.statut).subscribe({
      next: incidents => {
        this.incidents.set(incidents);
        this.charge.set(true);
      },
      error: (erreur: HttpErrorResponse) =>
        this.erreur.set(erreur.error?.detail ?? 'Impossible de charger les incidents.'),
    });
  }
}
