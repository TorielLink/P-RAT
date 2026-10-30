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

/** Page /stations/:id  */
@Component({
  selector: 'app-station',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './station.html',
  styleUrl: 'station.css'
})
export class StationPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly stationService = inject(StationService);
  private readonly equipementService = inject(EquipementService);
  private readonly fb = inject(FormBuilder).nonNullable;
  protected readonly authService = inject(AuthService);

  protected readonly types = TYPES_EQUIPEMENT;
  protected readonly etats = ETATS_EQUIPEMENT;
  readonly station = signal<Station | null>(null);
  readonly equipements = signal<Equipement[]>([]);
  readonly enModification = signal(false);
  readonly erreur = signal<string | null>(null);

  readonly nom = this.fb.control('', [Validators.required, Validators.maxLength(100)]);
  readonly formulaire = this.fb.group({
    reference: ['', Validators.pattern(/^[A-Z0-9]{0,8}$/)],
    description: [''],
    type: ['CAMERA' as TypeEquipement, Validators.required],
    etat: ['EN_SERVICE' as EtatEquipement, Validators.required],
  });

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.erreur.set(null);
      this.enModification.set(false);
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
    this.equipementService.obtenirEquipementsParStation(stationId).subscribe({
      next: equipements => this.equipements.set(equipements),
      error: (erreur: HttpErrorResponse) =>
        this.erreur.set(this.message(erreur, 'Impossible de charger les équipements.')),
    });
  }

  modifier(station: Station): void {
    this.nom.setValue(station.nom);
    this.erreur.set(null);
    this.enModification.set(true);
  }

  annuler(): void {
    this.enModification.set(false);
    this.erreur.set(null);
  }

  enregistrer(station: Station): void {
    if (this.nom.invalid) {
      this.erreur.set('Le nom est obligatoire (100 caractères maximum).');
      return;
    }
    this.stationService.modifierStation(station.id, this.nom.value.trim()).subscribe({
      next: modifiee => {
        this.station.set(modifiee);
        this.enModification.set(false);
        this.erreur.set(null);
      },
      error: (erreur: HttpErrorResponse) => this.erreur.set(this.message(erreur, 'Échec de la modification.')),
    });
  }

  ajouter(station: Station): void {
    if (this.formulaire.invalid) {
      this.erreur.set('La référence contient au plus 8 lettres majuscules ou chiffres.');
      return;
    }
    const valeurs = this.formulaire.getRawValue();
    this.equipementService
      .ajouterEquipement({
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
    this.equipementService.supprimerEquipement(equipement.id).subscribe({
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
