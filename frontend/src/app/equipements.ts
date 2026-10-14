import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {Equipement, EquipementService} from './core/equipement.service';

interface Ligne {
  id?: number;
  reference: string;
  libelle: string;
  station: string;
  etat: string;
  type: string;
}

@Component({
  selector: 'app-equipements',
  imports: [FormsModule],
  template: `
    <h2>Équipements</h2>

    <input [(ngModel)]="nouveau.reference" placeholder="Référence" />
    <input [(ngModel)]="nouveau.libelle" placeholder="Libellé" />
    <input [(ngModel)]="nouveau.station" placeholder="Station" />
    <input [(ngModel)]="nouveau.etat" placeholder="État" />
    <input [(ngModel)]="nouveau.type" placeholder="Type" />
    <button (click)="ajouter()" [disabled]="!formulaireValide()">Ajouter</button>

    @if (erreur()) {
      <p>{{ erreur() }}</p>
    }

    <ul>
      @for (e of equipements(); track e.id) {
        <li>{{ e.reference }} — {{ e.libelle }} ({{ e.station }}, {{ e.etat }})</li>
      }
    </ul>
  `,
})
export class Equipements implements OnInit {
  private service = inject(EquipementService);

  nouveau: Ligne = { reference: '', libelle: '', station: '', etat: '', type: '' };
  equipements = signal<Ligne[]>([]);
  erreur = signal('');

  ngOnInit() {
    this.charger();
  }

  formulaireValide() {
    return this.nouveau.reference.trim() !== '' && this.nouveau.libelle.trim() !== '';
  }

  charger() {
    this.service.getAll().subscribe({
      next: (liste) => this.equipements.set(liste),
      error: () => this.erreur.set('Impossible de charger la liste (backend sur le port 8080 ?)'),
    });
  }

  ajouter() {
    this.erreur.set('');
    this.service.add(<Equipement>this.nouveau).subscribe({
      next: () => {
        this.nouveau = { reference: '', libelle: '', station: '', etat: '', type:'' };
        this.charger();
      },
      error: () => this.erreur.set("Échec de l'ajout"),
    });
  }
}
