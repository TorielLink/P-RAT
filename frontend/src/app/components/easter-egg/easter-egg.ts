import { Component, inject, signal } from '@angular/core';
import { DUREE_EFFET_MS, KonamiService } from '../../core/easter-egg/konami.service';
import { jouerCarillon } from '../../shared/carillon';

const BALISES_DE_SAISIE = ['INPUT', 'TEXTAREA', 'SELECT'];
const COULEURS_PEINTURE = ['--rouge-trait', '--jaune', '--vert', '--orange', '--bleu', '--accent-survol'];

interface Tache {
  gauche: number;  // en % de la largeur du wagon
  haut: number;    // en % de la hauteur du wagon
  taille: number;  // en px
  couleur: string;
  coulure: boolean;
}

@Component({
  selector: 'app-easter-egg',
  templateUrl: './easter-egg.html',
  styleUrl: './easter-egg.scss',
  host: { '(document:keydown)': 'surTouche($event)' },
})
export class EasterEgg {
  protected readonly konami = inject(KonamiService);
  protected readonly dureeTraversee = `${DUREE_EFFET_MS}ms`;
  protected readonly peinture = signal<Tache[][]>([[], [], []]);

  protected surTouche(evenement: KeyboardEvent): void {
    if (EasterEgg.estUnChampDeSaisie(evenement.target)) {
      return;
    }
    if (this.konami.enregistrerTouche(evenement.key)) {
      this.peinture.set(EasterEgg.peindreLaRame());
      jouerCarillon();
    }
  }

  // taper "ba" dans une description ne déclenche pas l'easter egg
  private static estUnChampDeSaisie(cible: EventTarget | null): boolean {
    return cible instanceof HTMLElement
      && (cible.isContentEditable || BALISES_DE_SAISIE.includes(cible.tagName));
  }

  private static peindreLaRame(): Tache[][] {
    const nombres = EasterEgg.melanger([1, 2, 3, 4]).slice(0, 3);
    return nombres.map(nombre => Array.from({ length: nombre }, () => EasterEgg.tacheAuHasard()));
  }

  private static tacheAuHasard(): Tache {
    return {
      gauche: EasterEgg.entre(12, 88),
      haut: EasterEgg.entre(25, 55),
      taille: Math.round(EasterEgg.entre(10, 20)),
      couleur: COULEURS_PEINTURE[Math.floor(Math.random() * COULEURS_PEINTURE.length)],
      coulure: Math.random() < 0.5,
    };
  }

  private static entre(min: number, max: number): number {
    return min + Math.random() * (max - min);
  }

  private static melanger<T>(valeurs: T[]): T[] {
    const copie = [...valeurs];
    for (let i = copie.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [copie[i], copie[j]] = [copie[j], copie[i]];
    }
    return copie;
  }
}
