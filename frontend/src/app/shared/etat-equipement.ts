import { Component, computed, input } from '@angular/core';
import { EtatEquipement } from '../core/equipement/equipement.service';

// Icône + libellé : l'état reste compréhensible sans la couleur (accessibilité)
const AFFICHAGE: Record<EtatEquipement, { icone: string; libelle: string }> = {
  EN_SERVICE: { icone: '✓', libelle: 'En service' },
  EN_PANNE: { icone: '✕', libelle: 'En panne' },
  EN_MAINTENANCE: { icone: '⚠', libelle: 'En maintenance' },
  HORS_SERVICE: { icone: '⊘', libelle: 'Hors service' },
};

@Component({
  selector: 'app-etat-equipement',
  template: `
    <span class="etat" [class]="'etat ' + etat()">
      <span class="icone" aria-hidden="true">{{ affichage().icone }}</span>
      {{ affichage().libelle }}
    </span>
  `,
  styles: `
    .etat {
      display: inline-flex;
      align-items: center;
      gap: 0.35em;
      padding: 0.1em 0.6em;
      border: 1px solid;
      border-radius: 1em;
      font-weight: 600;
      white-space: nowrap;
    }
    .icone {
      font-weight: 700;
    }
    /* Texte foncé sur fond clair : contraste suffisant (WCAG AA) */
    .EN_SERVICE { color: #1a7f37; background: #dafbe1; }
    .EN_PANNE { color: #cf222e; background: #ffebe9; }
    .EN_MAINTENANCE { color: #9a6700; background: #fff8c5; }
    .HORS_SERVICE { color: #57606a; background: #eaeef2; }
  `,
})
export class EtatEquipementComponent {
  readonly etat = input.required<EtatEquipement>();
  readonly affichage = computed(() => AFFICHAGE[this.etat()]);
}
