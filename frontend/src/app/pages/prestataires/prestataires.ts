import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { Prestataire, PrestataireService } from '../../core/prestataire/prestataire.service';
import { formaterNoteMoyenne } from '../../shared/formater-note';

/** /prestataires : note moyenne et nombre d'évaluations de chaque prestataire */
@Component({
  selector: 'app-prestataires',
  imports: [RouterLink],
  templateUrl: './prestataires.html',
  styleUrl: './prestataires.scss',
})
export class Prestataires implements OnInit {
  private readonly prestataireService = inject(PrestataireService);
  protected readonly formaterNoteMoyenne = formaterNoteMoyenne;

  readonly prestataires = signal<Prestataire[]>([]);
  readonly erreur = signal<string | null>(null);

  ngOnInit(): void {
    this.prestataireService.obtenirTousPrestataires().subscribe({
      next: prestataires => this.prestataires.set(prestataires),
      error: (erreur: HttpErrorResponse) =>
        this.erreur.set(erreur.error?.detail ?? 'Impossible de charger les prestataires.'),
    });
  }
}
