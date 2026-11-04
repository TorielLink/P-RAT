import { Component, inject } from '@angular/core';
import { DUREE_EFFET_MS, KonamiService } from '../../core/easter-egg/konami.service';
import { jouerCarillon } from '../../shared/carillon';

const BALISES_DE_SAISIE = ['INPUT', 'TEXTAREA', 'SELECT'];

@Component({
  selector: 'app-easter-egg',
  templateUrl: './easter-egg.html',
  styleUrl: './easter-egg.scss',
  host: { '(document:keydown)': 'surTouche($event)' },
})
export class EasterEgg {
  protected readonly konami = inject(KonamiService);
  protected readonly dureeTraversee = `${DUREE_EFFET_MS}ms`;

  protected surTouche(evenement: KeyboardEvent): void {
    if (EasterEgg.estUnChampDeSaisie(evenement.target)) {
      return;
    }
    if (this.konami.enregistrerTouche(evenement.key)) {
      jouerCarillon();
    }
  }

  // taper "ba" dans une description ne déclenche pas l'easter egg
  private static estUnChampDeSaisie(cible: EventTarget | null): boolean {
    return cible instanceof HTMLElement
      && (cible.isContentEditable || BALISES_DE_SAISIE.includes(cible.tagName));
  }
}
