const MINUTES_PAR_HEURE = 60;
const MINUTES_PAR_JOUR = 24 * MINUTES_PAR_HEURE;

/** 3 060 minutes → « 2 j 3 h » ; 75 → « 1 h 15 min » ; 20 → « 20 min ». */
export function formaterDuree(totalMinutes: number): string {
  const jours = Math.floor(totalMinutes / MINUTES_PAR_JOUR);
  const heures = Math.floor((totalMinutes % MINUTES_PAR_JOUR) / MINUTES_PAR_HEURE);
  const minutes = totalMinutes % MINUTES_PAR_HEURE;
  if (jours > 0) {
    return `${jours} j ${heures} h`;
  }
  if (heures > 0) {
    return `${heures} h ${minutes} min`;
  }
  return `${minutes} min`;
}
