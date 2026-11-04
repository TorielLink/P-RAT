import { Pipe, PipeTransform } from '@angular/core';
import { EtatEquipement, TypeEquipement } from '../core/equipement/equipement.service';
import { LIBELLES_CHAMP, LIBELLES_EVENEMENT, LIBELLES_STATUT, PrioriteIncident } from '../core/incident/incident.service';

const LIBELLES_TYPE: Record<TypeEquipement, string> = {
  CAMERA: 'Caméra',
  ASCENSEUR: 'Ascenseur',
  PORTE: 'Porte',
  ESCALATOR: 'Escalator',
};

const LIBELLES_ETAT: Record<EtatEquipement, string> = {
  EN_SERVICE: 'En service',
  EN_PANNE: 'En panne',
  EN_MAINTENANCE: 'En maintenance',
  HORS_SERVICE: 'Hors service',
};

const LIBELLES_PRIORITE: Record<PrioriteIncident, string> = {
  BASSE: 'Basse',
  MOYENNE: 'Moyenne',
  HAUTE: 'Haute',
  CRITIQUE: 'Critique',
};

// Les codes des quatre enums sont tous différents : un seul dictionnaire suffit
const LIBELLES: Record<string, string> = {
  ...LIBELLES_TYPE,
  ...LIBELLES_ETAT,
  ...LIBELLES_PRIORITE,
  ...LIBELLES_STATUT,
  ...LIBELLES_EVENEMENT,
  ...LIBELLES_CHAMP,
};

/** {{ 'EN_PANNE' | libelle }} → « En panne ». Un code inconnu est affiché tel quel ; null → « — ». */
@Pipe({ name: 'libelle' })
export class LibellePipe implements PipeTransform {
  transform(code: string | null | undefined): string {
    if (code === null || code === undefined) {
      return '—';
    }
    return LIBELLES[code] ?? code;
  }
}
