import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { SituationStation, StationCarte, StationService } from '../../core/station/station.service';
import { NiveauAttentionComponent } from '../../shared/niveau-attention';

// Couleur doublée d'un symbole (accessibilité) : mêmes symboles que le badge de niveau
const AFFICHAGE_SITUATION: Record<SituationStation, { couleur: string; symbole: string; libelle: string }> = {
  ROUGE: { couleur: '#da3633', symbole: '◆', libelle: 'Rouge' },
  ORANGE: { couleur: '#bd561d', symbole: '▲', libelle: 'Orange' },
  VERT: { couleur: '#238636', symbole: '●', libelle: 'Vert' },
  PRIS_EN_CHARGE: { couleur: '#1f6feb', symbole: '◐', libelle: 'Pris en charge' },
  AUCUN: { couleur: '#6e7681', symbole: '○', libelle: 'Aucun incident' },
};

const TAILLE = 600; // côté du dessin SVG, en unités du viewBox
const MARGE = 2; // cases ajoutées autour des stations
const ETENDUE_MIN = 6; // évite un zoom extrême avec une seule station

/** Page /carte (superviseur) : plan schématique du réseau en grille. */
@Component({
  selector: 'app-carte',
  imports: [RouterLink, NiveauAttentionComponent],
  templateUrl: './carte.html',
  styleUrl: './carte.scss',
})
export class Carte implements OnInit {
  private readonly stationService = inject(StationService);

  protected readonly taille = TAILLE;
  protected readonly affichage = AFFICHAGE_SITUATION;
  protected readonly situations = Object.keys(AFFICHAGE_SITUATION) as SituationStation[];

  readonly stations = signal<StationCarte[]>([]);
  readonly selection = signal<StationCarte | null>(null);
  readonly erreur = signal<string | null>(null);

  readonly placees = computed(() => this.stations().filter(s => s.positionX !== null && s.positionY !== null));
  readonly nombreSansPosition = computed(() => this.stations().length - this.placees().length);

  /** Cadrage automatique : carré englobant toutes les stations placées, avec une marge. */
  readonly cadre = computed(() => {
    const placees = this.placees();
    if (placees.length === 0) {
      return { minX: -10, maxY: 10, etendue: 20 };
    }
    const xs = placees.map(s => s.positionX!);
    const ys = placees.map(s => s.positionY!);
    const etendue = Math.max(Math.max(...xs) - Math.min(...xs), Math.max(...ys) - Math.min(...ys), ETENDUE_MIN) + 2 * MARGE;
    // Centre le carré sur les stations
    const centreX = (Math.max(...xs) + Math.min(...xs)) / 2;
    const centreY = (Math.max(...ys) + Math.min(...ys)) / 2;
    return { minX: centreX - etendue / 2, maxY: centreY + etendue / 2, etendue };
  });

  /** Lignes de la grille : une par unité visible. */
  readonly lignes = computed(() => {
    const { minX, maxY, etendue } = this.cadre();
    const verticales: { valeur: number; position: number }[] = [];
    for (let v = Math.ceil(minX); v <= Math.floor(minX + etendue); v++) {
      verticales.push({ valeur: v, position: this.versX(v) });
    }
    const horizontales: { valeur: number; position: number }[] = [];
    for (let v = Math.ceil(maxY - etendue); v <= Math.floor(maxY); v++) {
      horizontales.push({ valeur: v, position: this.versY(v) });
    }
    return { verticales, horizontales };
  });

  ngOnInit(): void {
    this.stationService.obtenirCarte().subscribe({
      next: stations => this.stations.set(stations),
      error: (erreur: HttpErrorResponse) =>
        this.erreur.set(
          erreur.status === 403
            ? 'La carte du réseau est réservée au superviseur.'
            : (erreur.error?.detail ?? 'Impossible de charger la carte.')
        ),
    });
  }

  /** Coordonnée de grille → coordonnée SVG (x vers la droite). */
  protected versX(x: number): number {
    const { minX, etendue } = this.cadre();
    return ((x - minX) / etendue) * TAILLE;
  }

  /** Coordonnée de grille → coordonnée SVG (y vers le haut, comme un plan : nord en haut). */
  protected versY(y: number): number {
    const { maxY, etendue } = this.cadre();
    return ((maxY - y) / etendue) * TAILLE;
  }

  selectionner(station: StationCarte, evenement?: Event): void {
    evenement?.preventDefault(); // la barre d'espace ne fait pas défiler la page
    this.selection.set(station);
  }

  fermer(): void {
    this.selection.set(null);
  }

  protected nombreIncidentsActifs(station: StationCarte): number {
    return station.rouge + station.orange + station.vert + station.prisEnCharge;
  }
}
