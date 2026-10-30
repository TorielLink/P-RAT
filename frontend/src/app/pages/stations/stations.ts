import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { Station, StationService } from '../../core/station/station.service';

/** Page /stations */
@Component({
  selector: 'app-stations',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './stations.html',
  styleUrl: './stations.scss',
})
export class Stations implements OnInit {
  private readonly stationService = inject(StationService);
  protected readonly authService = inject(AuthService);

  readonly stations = signal<Station[]>([]);
  readonly erreur = signal<string | null>(null);

  readonly formulaire = inject(FormBuilder).nonNullable.group({
    nom: ['', [Validators.required, Validators.maxLength(100)]],
  });

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.stationService.obtenirToutesStations().subscribe({
      next: stations => this.stations.set(stations),
      error: (erreur: HttpErrorResponse) => this.erreur.set(this.message(erreur, 'Impossible de charger les stations.')),
    });
  }

  ajouter(): void {
    if (this.formulaire.invalid) {
      this.erreur.set('Le nom est obligatoire (100 caractères maximum).');
      return;
    }
    this.stationService.ajouterStation(this.formulaire.getRawValue().nom.trim()).subscribe({
      next: () => {
        this.formulaire.reset();
        this.erreur.set(null);
        this.charger();
      },
      error: (erreur: HttpErrorResponse) => this.erreur.set(this.message(erreur, "Échec de l'ajout.")),
    });
  }

  supprimer(station: Station): void {
    if (!confirm(`Supprimer « ${station.nom} » ainsi que ses équipements et leurs incidents ?`)) {
      return;
    }
    this.stationService.supprimerStation(station.id).subscribe({
      next: () => this.charger(),
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
