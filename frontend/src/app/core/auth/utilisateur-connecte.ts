export type CodeProfil = 'ADMIN' | 'PRESTATAIRE' | 'AGENT';

export interface UtilisateurConnecte {
  prenom: string;
  profil: CodeProfil;
  profilLibelle: string;
}

export const ROUTE_ACCUEIL_PAR_PROFIL: Record<CodeProfil, string> = {
  ADMIN: '/tableau-de-bord',
  PRESTATAIRE: '/tableau-de-bord',
  AGENT: '/stations',
};
