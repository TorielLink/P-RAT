import { AbstractControl, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';

/** Bornes de la grille de la carte : mêmes valeurs que les CHECK de la V5 et les @Min/@Max de Station. */
export const POSITION_MIN = -20;
export const POSITION_MAX = 20;

export const VALIDATEURS_POSITION = [Validators.min(POSITION_MIN), Validators.max(POSITION_MAX)];

/** Sur le groupe : les deux coordonnées ou aucune (pas de demi-position). */
export const positionCompleteValidator: ValidatorFn = (groupe: AbstractControl): ValidationErrors | null => {
  const x = groupe.get('positionX')?.value;
  const y = groupe.get('positionY')?.value;
  const renseigne = (valeur: unknown) => valeur !== null && valeur !== '' && valeur !== undefined;
  return renseigne(x) === renseigne(y) ? null : { positionIncomplete: true };
};

/** Message d'erreur commun aux formulaires de station, ou null si tout est valide. */
export function messagePosition(groupe: AbstractControl): string | null {
  if (groupe.hasError('positionIncomplete')) {
    return 'Renseignez les deux coordonnées X et Y, ou aucune.';
  }
  if (groupe.get('positionX')?.invalid || groupe.get('positionY')?.invalid) {
    return `Les coordonnées doivent être comprises entre ${POSITION_MIN} et ${POSITION_MAX}.`;
  }
  return null;
}
