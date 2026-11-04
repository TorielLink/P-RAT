// trois notes (mi, do, sol) douces et courtes
const NOTES_HZ = [659.25, 523.25, 783.99];
const DUREE_NOTE_S = 0.35;
const VOLUME_MAX = 0.2;

export function jouerCarillon(): void {
  const contexte = new AudioContext();
  NOTES_HZ.forEach((frequence, rang) => jouerNote(contexte, frequence, contexte.currentTime + rang * DUREE_NOTE_S));
  setTimeout(() => contexte.close(), NOTES_HZ.length * DUREE_NOTE_S * 1000 + 100);
}

function jouerNote(contexte: AudioContext, frequence: number, debut: number): void {
  const oscillateur = contexte.createOscillator();
  const volume = contexte.createGain();
  oscillateur.type = 'sine';
  oscillateur.frequency.value = frequence;
  // montée et descente rapides du volume : un son de cloche plutôt qu'un « bip »
  volume.gain.setValueAtTime(0.0001, debut);
  volume.gain.exponentialRampToValueAtTime(VOLUME_MAX, debut + 0.02);
  volume.gain.exponentialRampToValueAtTime(0.0001, debut + DUREE_NOTE_S);
  oscillateur.connect(volume).connect(contexte.destination);
  oscillateur.start(debut);
  oscillateur.stop(debut + DUREE_NOTE_S);
}
