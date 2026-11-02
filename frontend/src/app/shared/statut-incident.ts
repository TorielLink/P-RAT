import { Component, computed, input } from '@angular/core';
import { StatutIncident } from '../core/incident/incident.service';

// Icône + libellé : le statut reste compréhensible sans la couleur
const AFFICHAGE: Record<StatutIncident, { icone: string; libelle: string }> = {
  OUVERT: { icone: '●', libelle: 'Ouvert' },
  EN_COURS: { icone: '◐', libelle: 'En cours' },
  RESOLU: { icone: '✓', libelle: 'Résolu' },
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
      border: 1px solid;
      border-radius: 1em;
      font-weight: 600;
      white-space: nowrap;
    }
    .OUVERT { color: #cf222e; background: #ffebe9; }
    .EN_COURS { color: #0550ae; background: #ddf4ff; }
    .RESOLU { color: #1a7f37; background: #dafbe1; }
  `,
})
export class StatutIncidentComponent {
  readonly statut = input.required<StatutIncident>();
  readonly affichage = computed(() => AFFICHAGE[this.statut()]);
}
