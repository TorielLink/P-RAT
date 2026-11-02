import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';
import {
  ETATS_EQUIPEMENT,
  Equipement,
  EquipementService,
  EtatEquipement,
  TYPES_EQUIPEMENT,
  TypeEquipement,
} from '../../core/equipement/equipement.service';
import { EtatEquipementComponent } from '../../shared/etat-equipement';

/** Page /equipements/:id */
@Component({
  selector: 'app-equipement',
  imports: [DatePipe, ReactiveFormsModule, RouterLink, EtatEquipementComponent],
  templateUrl: './equipement.html',
})
export class EquipementPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly equipementService = inject(EquipementService);
  protected readonly authService = inject(AuthService);

  protected readonly etats = ETATS_EQUIPEMENT;
  protected readonly types = TYPES_EQUIPEMENT;
  readonly equipement = signal<Equipement | null>(null);
  readonly enModification = signal(false);
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
      this.enModification.set(false);
      this.equipementService.obtenirEquipementParId(Number(params.get('id'))).subscribe({
        next: equipement => this.equipement.set(equipement),
        error: (erreur: HttpErrorResponse) => {
          this.equipement.set(null);
          this.erreur.set(this.message(erreur, 'Équipement introuvable.'));
        },
      });
    });
  }

  modifier(equipement: Equipement): void {
    // Le formulaire part des valeurs actuelles de l'équipement
    this.formulaire.setValue({
      reference: equipement.reference ?? '',
      description: equipement.description ?? '',
      type: equipement.type,
      etat: equipement.etat,
    });
    this.erreur.set(null);
    this.enModification.set(true);
  }

  annuler(): void {
    this.enModification.set(false);
    this.erreur.set(null);
  }

  enregistrer(equipement: Equipement): void {
    if (this.formulaire.invalid) {
      this.erreur.set('La référence contient au plus 8 lettres majuscules ou chiffres.');
      return;
    }
    const valeurs = this.formulaire.getRawValue();

    // Admin et agent : modification complète (PUT) ; prestataire : état seul (PATCH)
    const requete: Observable<Equipement> = this.authService.peutGererEquipements()
      ? this.equipementService.modifierEquipement(equipement.id, {
        reference: valeurs.reference.trim() || null,
        description: valeurs.description.trim() || null,
        type: valeurs.type,
        etat: valeurs.etat,
        stationId: equipement.stationId,
      })
      : this.equipementService.changerEtatEquipement(equipement.id, valeurs.etat);

    requete.subscribe({
      next: modifie => {
        this.equipement.set(modifie);
        this.enModification.set(false);
        this.erreur.set(null);
      },
      error: (erreur: HttpErrorResponse) => this.erreur.set(this.message(erreur, 'Échec de la modification.')),
    });
  }

  supprimer(equipement: Equipement): void {
    if (!confirm(`Supprimer l'équipement ${equipement.reference ?? 'n°' + equipement.id} et ses incidents ?`)) {
      return;
    }
    this.equipementService.supprimerEquipement(equipement.id).subscribe({
      next: () => this.router.navigate(['/equipements']),
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
