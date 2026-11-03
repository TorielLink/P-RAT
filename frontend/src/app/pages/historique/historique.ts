import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HistoriqueIncident, IncidentService, Page } from '../../core/incident/incident.service';
import { formaterDuree } from '../../shared/formater-duree';

/** Page /incidents/historique?page=N : incidents résolus, du plus récemment résolu au plus ancien (RG7). */
@Component({
  selector: 'app-historique',
  imports: [DatePipe, RouterLink],
  templateUrl: './historique.html',
  styleUrl: './historique.scss',
})
export class Historique implements OnInit {
  private readonly incidentService = inject(IncidentService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly formaterDuree = formaterDuree;
  readonly historique = signal<Page<HistoriqueIncident> | null>(null);
  readonly erreur = signal<string | null>(null);

  ngOnInit(): void {
    // La page courante est dans l'URL : le bouton Retour du navigateur revient à la bonne page
    this.route.queryParamMap.subscribe(params => {
      const page = Math.max(0, Number(params.get('page') ?? 0) || 0);
      this.incidentService.obtenirHistorique(page).subscribe({
        next: resultat => {
          this.historique.set(resultat);
          this.erreur.set(null);
        },
        error: (erreur: HttpErrorResponse) =>
          this.erreur.set(erreur.error?.detail ?? "Impossible de charger l'historique."),
      });
    });
  }

  allerALaPage(page: number): void {
    this.router.navigate([], { relativeTo: this.route, queryParams: { page } });
  }

  protected abreger(texte: string | null): string {
    if (!texte) {
      return '—';
    }
    return texte.length > 30 ? texte.slice(0, 30) + '…' : texte;
  }
}
