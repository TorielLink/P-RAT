import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { IncidentActif, IncidentService } from '../../core/incident/incident.service';
import { TYPES_EQUIPEMENT } from '../../core/equipement/equipement.service';
import { NiveauAttentionComponent } from '../../shared/niveau-attention';
import { formaterDuree } from '../../shared/formater-duree';
import { AuthService } from '../../core/auth/auth.service';
import { LibellePipe } from '../../shared/libelle.pipe';

type Tri = 'URGENCE' | 'PLUS_ANCIENS' | 'PLUS_RECENTS';

// Ordre d'urgence pour le tri par défaut : rouge d'abord, pris en charge en dernier
const RANG_URGENCE: Record<IncidentActif['niveauAttention'], number> = {
  ROUGE: 0,
  ORANGE: 1,
  VERT: 2,
  PRIS_EN_CHARGE: 3,
};

/** Page /tableau-de-bord (superviseur) : incidents non résolus, filtres et indicateurs. */
@Component({
  selector: 'app-tableau-de-bord',
  imports: [RouterLink, NiveauAttentionComponent, LibellePipe],
  templateUrl: './tableau-de-bord.html',
  styleUrl: './tableau-de-bord.scss',
})
export class TableauDeBord implements OnInit {
  private readonly incidentService = inject(IncidentService);

  protected readonly formaterDuree = formaterDuree;
  protected readonly types = TYPES_EQUIPEMENT;
  protected readonly authService = inject(AuthService);

  readonly incidents = signal<IncidentActif[]>([]);
  readonly erreur = signal<string | null>(null);

  // Filtres ('' = pas de filtre) et tri
  readonly filtreNiveau = signal('');
  readonly filtreStation = signal('');
  readonly filtreType = signal('');
  readonly filtreAffectation = signal('');
  readonly tri = signal<Tri>('URGENCE');

  /** Stations présentes parmi les incidents actifs, pour remplir le filtre. */
  readonly stations = computed(() => {
    const parId = new Map<number, string>();
    for (const incident of this.incidents()) {
      if (incident.stationId !== null) {
        parId.set(incident.stationId, incident.stationNom ?? '');
      }
    }
    return [...parId].map(([id, nom]) => ({ id, nom })).sort((a, b) => a.nom.localeCompare(b.nom));
  });

  /** Incidents filtrés puis triés : recalculés à chaque changement de filtre. */
  readonly affiches = computed(() => {
    const filtres = this.incidents().filter(
      incident =>
        (!this.filtreNiveau() || incident.niveauAttention === this.filtreNiveau()) &&
        (!this.filtreStation() || String(incident.stationId) === this.filtreStation()) &&
        (!this.filtreType() || incident.equipementType === this.filtreType()) &&
        (!this.filtreAffectation() ||
          (this.filtreAffectation() === 'AFFECTE') === (incident.prestataireId !== null))
    );
    return filtres.sort(this.comparateur(this.tri()));
  });

  /** Indicateurs calculés sur les incidents affichés : ils suivent les filtres. */
  readonly indicateurs = computed(() => {
    const liste = this.affiches();
    const compter = (niveau: IncidentActif['niveauAttention']) =>
      liste.filter(incident => incident.niveauAttention === niveau).length;
    const totalMinutes = liste.reduce((somme, incident) => somme + incident.dureeOuvertureMinutes, 0);
    return {
      actifs: liste.length,
      vert: compter('VERT'),
      orange: compter('ORANGE'),
      rouge: compter('ROUGE'),
      prisEnCharge: compter('PRIS_EN_CHARGE'),
      dureeMoyenne: liste.length ? formaterDuree(Math.round(totalMinutes / liste.length)) : '—',
    };
  });

  readonly filtresActifs = computed(
    () => !!(this.filtreNiveau() || this.filtreStation() || this.filtreType() || this.filtreAffectation())
  );

  ngOnInit(): void {
    this.incidentService.obtenirIncidentsActifs().subscribe({
      next: incidents => this.incidents.set(incidents),
      error: (erreur: HttpErrorResponse) =>
        this.erreur.set(
          erreur.status === 403
            ? "Votre profil n'a pas accès au tableau de bord."
            : (erreur.error?.detail ?? 'Impossible de charger le tableau de bord.')
        ),
    });
  }

  reinitialiser(): void {
    this.filtreNiveau.set('');
    this.filtreStation.set('');
    this.filtreType.set('');
    this.filtreAffectation.set('');
    this.tri.set('URGENCE');
  }

  /** Lit la valeur d'un <select> (évite un $any dans le template). */
  protected valeur(evenement: Event): string {
    return (evenement.target as HTMLSelectElement).value;
  }

  protected abreger(texte: string | null): string {
    if (!texte) {
      return '—';
    }
    return texte.length > 60 ? texte.slice(0, 60) + '…' : texte;
  }

  private comparateur(tri: Tri): (a: IncidentActif, b: IncidentActif) => number {
    const plusAnciensDabord = (a: IncidentActif, b: IncidentActif) =>
      b.dureeOuvertureMinutes - a.dureeOuvertureMinutes || a.id - b.id;
    switch (tri) {
      case 'URGENCE':
        return (a, b) => RANG_URGENCE[a.niveauAttention] - RANG_URGENCE[b.niveauAttention] || plusAnciensDabord(a, b);
      case 'PLUS_ANCIENS':
        return plusAnciensDabord;
      case 'PLUS_RECENTS':
        return (a, b) => -plusAnciensDabord(a, b);
    }
  }
}
