import { Injectable, signal } from '@angular/core';

export const SEQUENCE_KONAMI = [
  'arrowup', 'arrowup', 'arrowdown', 'arrowdown',
  'arrowleft', 'arrowright', 'arrowleft', 'arrowright',
  'b', 'a',
] as const;

export const DUREE_EFFET_MS = 4000;

@Injectable({ providedIn: 'root' })
export class KonamiService {
  readonly actif = signal(false);
  private dernieresTouches: string[] = [];

  /** Renvoie true au moment précis où la séquence vient d'être complétée. */
  enregistrerTouche(touche: string): boolean {
    this.dernieresTouches = [...this.dernieresTouches, touche.toLowerCase()].slice(-SEQUENCE_KONAMI.length);
    if (this.actif() || this.dernieresTouches.join(',') !== SEQUENCE_KONAMI.join(',')) {
      return false;
    }
    this.dernieresTouches = [];
    this.actif.set(true);
    setTimeout(() => this.actif.set(false), DUREE_EFFET_MS);
    return true;
  }
}
