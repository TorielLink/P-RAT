import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FichePrestataire, PrestataireService } from '../../core/prestataire/prestataire.service';
import { formaterNoteMoyenne } from '../../shared/formater-note';

/** /prestataires/:id : note moyenne et évaluations d'un prestataire */
@Component({
  selector: 'app-prestataire',
  imports: [DatePipe, RouterLink],
  templateUrl: './prestataire.html',
  styleUrl: './prestataire.scss',
})
export class PrestatairePage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly prestataireService = inject(PrestataireService);
  protected readonly formaterNoteMoyenne = formaterNoteMoyenne;

  readonly fiche = signal<FichePrestataire | null>(null);
  readonly erreur = signal<string | null>(null);

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.erreur.set(null);
      this.prestataireService.obtenirFiche(Number(params.get('id'))).subscribe({
        next: fiche => this.fiche.set(fiche),
        error: (erreur: HttpErrorResponse) => {
          this.fiche.set(null);
          this.erreur.set(erreur.error?.detail ?? 'Prestataire introuvable.');
        },
      });
    });
  }
}
