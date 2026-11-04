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
      border: 1px solid transparent;
      border-radius: 1em;
      font-weight: 600;
      white-space: nowrap;
    }
    .icone {
      font-weight: 700;
    }
    /* Texte clair sur fond foncé : contraste suffisant (WCAG AA) */
    .EN_SERVICE { color: var(--vert); background: var(--vert-fond); }
    .EN_PANNE { color: var(--rouge); background: var(--rouge-fond); }
    .EN_MAINTENANCE { color: var(--jaune); background: var(--jaune-fond); }
    .HORS_SERVICE { color: var(--gris); background: var(--gris-fond); }
  `,
})
export class EtatEquipementComponent {
  readonly etat = input.required<EtatEquipement>();
  readonly affichage = computed(() => AFFICHAGE[this.etat()]);
}
