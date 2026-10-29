import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { Station, StationService } from '../../core/station/station.service';
import {
  ETATS_EQUIPEMENT,
  Equipement,
  EquipementService,
  EtatEquipement,
  TYPES_EQUIPEMENT,
  TypeEquipement,
} from '../../core/equipement/equipement.service';

/** Page /stations/:id */
@Component({
  selector: 'app-station',
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    @if (station(); as st) {
      <h1>{{ st.nom }}</h1>

      @if (authService.PeutGererEquipements()) {
        <h2>Ajouter un équipement</h2>
        <form [formGroup]="formulaire" (ngSubmit)="ajouter(st)">
          <input formControlName="reference" placeholder="Référence (ex. CAM00001)" maxlength="8" />
          <input formControlName="description" placeholder="Description" maxlength="255" />
          <select formControlName="type">
            @for (type of types; track type) {
              <option [ngValue]="type">{{ type }}</option>
            }
          </select>
          <select formControlName="etat">
            @for (etat of etats; track etat) {
              <option [ngValue]="etat">{{ etat }}</option>
            }
          </select>
          <button type="submit">Ajouter</button>
        </form>
      }

      <h2>Équipements</h2>
      @if (equipements().length === 0) {
        <p>Aucun équipement dans cette station.</p>
      } @else {
        <table>
          <tr>
            <th>Référence</th>
            <th>Type</th>
            <th>Description</th>
            <th>État</th>
            <th></th>
          </tr>
          @for (eq of equipements(); track eq.id) {
            <tr>
              <td><a [routerLink]="['/equipements', eq.id]">{{ eq.reference ?? 'n°' + eq.id }}</a></td>
              <td>{{ eq.type }}</td>
              <td>{{ eq.description }}</td>
              <td>{{ eq.etat }}</td>
              <td>
                @if (authService.PeutGererEquipements()) {
                  <button type="button" (click)="supprimer(eq)">Supprimer</button>
                }
              </td>
            </tr>
          }
        </table>
      }
    }

    @if (erreur()) {
      <p>{{ erreur() }}</p>
    }
  `,
})
export class StationPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly stationService = inject(StationService);
  private readonly equipementService = inject(EquipementService);
  protected readonly authService = inject(AuthService);

  protected readonly types = TYPES_EQUIPEMENT;
  protected readonly etats = ETATS_EQUIPEMENT;
  readonly station = signal<Station | null>(null);
  readonly equipements = signal<Equipement[]>([]);
  readonly erreur = signal<string | null>(null);

  readonly formulaire = inject(FormBuilder).nonNullable.group({
    reference: ['', Validators.pattern(/^[A-Z0-9]{0,8}$/)],
    description: [''],
    type: ['CAMERA' as TypeEquipement, Validators.required],
    etat: ['EN_SERVICE' as EtatEquipement, Validators.required],
  });

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.erreur.set(null);
      this.stationService.obtenirStationParId(Number(params.get('id'))).subscribe({
        next: station => {
          this.station.set(station);
          this.chargerEquipements(station.id);
        },
        error: (erreur: HttpErrorResponse) => {
          this.station.set(null);
          this.erreur.set(this.message(erreur, 'Station introuvable.'));
        },
      });
    });
  }

  chargerEquipements(stationId: number): void {
    this.equipementService.ObtenirEquipementsParStation(stationId).subscribe({
      next: equipements => this.equipements.set(equipements),
      error: (erreur: HttpErrorResponse) =>
        this.erreur.set(this.message(erreur, 'Impossible de charger les équipements.')),
    });
  }

  ajouter(station: Station): void {
    if (this.formulaire.invalid) {
      this.erreur.set('La référence contient au plus 8 lettres majuscules ou chiffres.');
      return;
    }
    const valeurs = this.formulaire.getRawValue();
    this.equipementService
      .AjouterEquipement({
        reference: valeurs.reference.trim() || null,
        description: valeurs.description.trim() || null,
        type: valeurs.type,
        etat: valeurs.etat,
        stationId: station.id, // rattaché d'office à la station affichée
      })
      .subscribe({
        next: () => {
          this.formulaire.reset();
          this.erreur.set(null);
          this.chargerEquipements(station.id);
        },
        error: (erreur: HttpErrorResponse) => this.erreur.set(this.message(erreur, "Échec de l'ajout.")),
      });
  }

  supprimer(equipement: Equipement): void {
    if (!confirm(`Supprimer l'équipement ${equipement.reference ?? 'n°' + equipement.id} et ses incidents ?`)) {
      return;
    }
    this.equipementService.SupprimerEquipement(equipement.id).subscribe({
      next: () => this.chargerEquipements(this.station()!.id),
      error: (erreur: HttpErrorResponse) => this.erreur.set(this.message(erreur, 'Échec de la suppression.')),
    });
  }

  private message(erreur: HttpErrorResponse, parDefaut: string): string {
    if (erreur.status === 403) {
      return 'Votre profil ne permet pas cette action.';
    }
    return erreur.error?.detail ?? erreur.error?.message ?? parDefaut;
  }
}
