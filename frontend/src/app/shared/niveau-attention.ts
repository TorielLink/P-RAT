import { Component, computed, input } from '@angular/core';
import { NiveauAttention } from '../core/incident/incident.service';


export const AFFICHAGE_NIVEAU: Record<NiveauAttention, { icone: string; libelle: string }> = {
  VERT: { icone: '●', libelle: 'Vert' },
  ORANGE: { icone: '▲', libelle: 'Orange' },
  ROUGE: { icone: '◆', libelle: 'Rouge' },
  PRIS_EN_CHARGE: { icone: '◐', libelle: 'Pris en charge' },
  RESOLU: { icone: '✓', libelle: 'Résolu' },
};

@Component({
  selector: 'app-niveau-attention',
  template: `
    <span [class]="'niveau ' + niveau()">
      <span aria-hidden="true">{{ affichage().icone }}</span>
      {{ affichage().libelle }}
    </span>
  `,
  styles: `
    .niveau {
      display: inline-flex;
      gap: 0.35em;
      padding: 0.1em 0.6em;
      border: 1px solid;
      border-radius: 1em;
      font-weight: 600;
      white-space: nowrap;
    }
    .VERT { color: #1a7f37; background: #dafbe1; }
    .ORANGE { color: #9a4e00; background: #fff1e5; }
    .ROUGE { color: #cf222e; background: #ffebe9; }
    .PRIS_EN_CHARGE { color: #0550ae; background: #ddf4ff; }
    .RESOLU { color: #57606a; background: #eaeef2; }
  `,
})
export class NiveauAttentionComponent {
  readonly niveau = input.required<NiveauAttention>();
  readonly affichage = computed(() => AFFICHAGE_NIVEAU[this.niveau()]);
}
