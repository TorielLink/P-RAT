import { Component, computed, input } from '@angular/core';
import { LIBELLES_STATUT, StatutIncident } from '../core/incident/incident.service';

// Icône + libellé : le statut reste compréhensible sans la couleur
const AFFICHAGE: Record<StatutIncident, { icone: string; libelle: string }> = {
  OUVERT: { icone: '●', libelle: LIBELLES_STATUT.OUVERT },
  EN_COURS: { icone: '◐', libelle: LIBELLES_STATUT.EN_COURS },
  RESOLU: { icone: '✓', libelle: LIBELLES_STATUT.RESOLU },
};

@Component({
  selector: 'app-statut-incident',
  template: `
    <span [class]="'statut ' + statut()">
      <span aria-hidden="true">{{ affichage().icone }}</span>
      {{ affichage().libelle }}
    </span>
  `,
  styles: `
    .statut {
      display: inline-flex;
      gap: 0.35em;
      padding: 0.1em 0.6em;
      border: 1px solid transparent;
      border-radius: 1em;
      font-weight: 600;
      white-space: nowrap;
    }
    .OUVERT { color: var(--rouge); background: var(--rouge-fond); }
    .EN_COURS { color: var(--bleu); background: var(--bleu-fond); }
    .RESOLU { color: var(--vert); background: var(--vert-fond); }
  `,
})
export class StatutIncidentComponent {
  readonly statut = input.required<StatutIncident>();
  readonly affichage = computed(() => AFFICHAGE[this.statut()]);
}
