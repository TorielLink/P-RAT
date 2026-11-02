import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { Equipement, EquipementService } from '../../core/equipement/equipement.service';
import { EtatEquipementComponent } from '../../shared/etat-equipement';


/** Page /equipements */
@Component({
  selector: 'app-equipements',
  imports: [RouterLink, EtatEquipementComponent],
  templateUrl: './equipements.html',
})
export class Equipements implements OnInit {
  private readonly equipementService = inject(EquipementService);

  readonly equipements = signal<Equipement[]>([]);
  readonly erreur = signal<string | null>(null);

  ngOnInit(): void {
    this.equipementService.obtenirTousEquipements().subscribe({
      next: equipements => this.equipements.set(equipements),
      error: (erreur: HttpErrorResponse) =>
        this.erreur.set(erreur.error?.message ?? 'Impossible de charger les équipements.'),
    });
  }
}
