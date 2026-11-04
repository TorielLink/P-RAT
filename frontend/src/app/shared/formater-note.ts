/** 4.5 → "4,5 / 5" et null → "Pas encore évalué" + arrondi à une décimale (front seulement) */
export function formaterNoteMoyenne(moyenne: number | null): string {
  if (moyenne === null) {
    return 'Pas encore évalué';
  }
  const arrondie = moyenne.toLocaleString('fr-FR', { minimumFractionDigits: 1, maximumFractionDigits: 1 });
  return `${arrondie} / 5`;
}
