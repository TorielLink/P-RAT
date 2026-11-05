import {Component, inject, OnInit, signal} from '@angular/core';
import {HttpErrorResponse, HttpStatusCode} from '@angular/common/http';
import {DatePipe} from '@angular/common';
import {FormBuilder, FormControl, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {AuthService} from '../../core/auth/auth.service';
import {Prestataire, PrestataireService} from '../../core/prestataire/prestataire.service';
import {
  COMMENTAIRE_MAX,
  DESCRIPTION_MAX,
  DESCRIPTION_MIN,
  EvenementIncident,
  Incident,
  IncidentDetail,
  IncidentService,
  NOTE_MAX,
  NOTE_MIN,
  NOTES_POSSIBLES,
  PrioriteIncident,
  PRIORITES_INCIDENT,
} from '../../core/incident/incident.service';
import {formaterDuree} from '../../shared/formater-duree';
import {LibellePipe} from '../../shared/libelle.pipe';
import {NiveauAttentionComponent} from '../../shared/niveau-attention';
import {StatutIncidentComponent} from '../../shared/statut-incident';



/** Page /incidents/:id : détail d'un incident. */
@Component({
  selector: 'app-incident',
  imports: [DatePipe, ReactiveFormsModule, RouterLink, LibellePipe, NiveauAttentionComponent, StatutIncidentComponent],
  templateUrl: './incident.html',
  styleUrl: 'incident.scss'
})
export class IncidentPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly incidentService = inject(IncidentService);
  protected readonly authService = inject(AuthService);
  readonly journal = signal<EvenementIncident[]>([]);

  protected readonly priorites = PRIORITES_INCIDENT;
  protected readonly formaterDuree = formaterDuree;

  protected readonly descriptionMin = DESCRIPTION_MIN;
  protected readonly descriptionMax = DESCRIPTION_MAX;
  readonly erreursServeur = signal<Record<string, string>>({});

  readonly incident = signal<IncidentDetail | null>(null);
  readonly enModification = signal(false);
  readonly erreur = signal<string | null>(null);

  private readonly prestataireService = inject(PrestataireService);
  readonly prestataires = signal<Prestataire[]>([]);
  readonly information = signal<string | null>(null);
  readonly choixPrestataire = new FormControl<number | null>(null);

  readonly formulaire = inject(FormBuilder).nonNullable.group({
    description: ['', [Validators.required, Validators.minLength(DESCRIPTION_MIN), Validators.maxLength(DESCRIPTION_MAX)]],
    priorite: ['MOYENNE' as PrioriteIncident, Validators.required],
  });

  protected readonly notesPossibles = NOTES_POSSIBLES;
  protected readonly commentaireMax = COMMENTAIRE_MAX;
  readonly erreursEvaluation = signal<Record<string, string>>({});

  readonly formulaireEvaluation = inject(FormBuilder).group({
    note: [null as number | null, [Validators.required, Validators.min(NOTE_MIN), Validators.max(NOTE_MAX)]],
    commentaire: ['', Validators.maxLength(COMMENTAIRE_MAX)],
  });

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.information.set(null);
      this.charger(Number(params.get('id')));
    });
    if (this.authService.estAdmin()) {
      this.prestataireService.obtenirTousPrestataires().subscribe({
        next: prestataires => this.prestataires.set(prestataires),
        error: (erreur: HttpErrorResponse) =>
          this.erreur.set(this.message(erreur, 'Impossible de charger les prestataires.')),
      });
    }
  }

  modifier(incident: Incident): void {
    this.formulaire.setValue({
      description: incident.description ?? '',
      priorite: incident.priorite ?? 'MOYENNE',
    });
    this.erreur.set(null);
    this.erreursServeur.set({});
    this.enModification.set(true);
  }

  annuler(): void {
    this.enModification.set(false);
    this.erreur.set(null);
  }

  enregistrer(incident: Incident): void {
    this.erreursServeur.set({});
    if (this.formulaire.invalid) {
      this.formulaire.markAllAsTouched();
      return;
    }
    const {description, priorite} = this.formulaire.getRawValue();
    this.incidentService.modifierIncident(incident.id, {description: description.trim(), priorite}).subscribe({
      next: () => this.charger(incident.id),
      error: (erreur: HttpErrorResponse) => this.afficherErreurModification(erreur),
    });
  }

  private afficherErreurModification(erreur: HttpErrorResponse): void {
    if (erreur.status === HttpStatusCode.BadRequest && erreur.error?.erreurs) {
      this.erreursServeur.set(erreur.error.erreurs);
      return;
    }
    this.erreur.set(this.message(erreur, 'Échec de la modification.'));
  }

  resoudre(incident: Incident): void {
    if (!confirm(`Marquer l'incident n°${incident.id} comme résolu ? Il ne pourra plus être modifié.`)) {
      return;
    }
    this.incidentService.resoudreIncident(incident.id).subscribe({
      next: () => this.charger(incident.id),
      error: (erreur: HttpErrorResponse) => this.erreur.set(this.message(erreur, 'Échec de la résolution.')),
    });
  }

  affecter(incident: IncidentDetail): void {
    const prestataireId = this.choixPrestataire.value;
    if (prestataireId === null) {
      return;
    }
    if (prestataireId === incident.prestataireId) {
      this.information.set(`L'incident est déjà affecté à ${incident.prestataireNom} -> aucune modification.`);
      return;
    }
    this.information.set(null);
    this.incidentService.affecterPrestataire(incident.id, prestataireId).subscribe({
      next: affecte => {
        this.information.set(`Incident affecté à ${affecte.prestataireNom}.`);
        this.charger(incident.id);
      },
      error: (erreur: HttpErrorResponse) => this.erreur.set(this.message(erreur, "Échec de l'affectation.")),
    });
  }

  peutEtreEvalue(incident: IncidentDetail): boolean {
    return this.authService.estAdmin()
      && incident.statut === 'RESOLU'
      && incident.prestataireId !== null
      && incident.evaluation === null;
  }

  evaluer(incident: IncidentDetail): void {
    this.erreursEvaluation.set({});
    if (this.formulaireEvaluation.invalid) {
      this.formulaireEvaluation.markAllAsTouched();
      return;
    }
    const { note, commentaire } = this.formulaireEvaluation.getRawValue();
    this.incidentService.evaluerIncident(incident.id, { note: note!, commentaire: commentaire?.trim() || null }).subscribe({
      next: () => {
        this.formulaireEvaluation.reset({ note: null, commentaire: '' });
        this.information.set('Évaluation enregistrée.');
        this.charger(incident.id);
      },
      error: (erreur: HttpErrorResponse) => {
        if (erreur.status === HttpStatusCode.BadRequest && erreur.error?.erreurs) {
          this.erreursEvaluation.set(erreur.error.erreurs);
          return;
        }
        this.erreur.set(this.message(erreur, "Échec de l'enregistrement de l'évaluation."));
      },
    });
  }

  supprimer(incident: Incident): void {
    if (!confirm(`Supprimer l'incident n°${incident.id} et son évaluation ?`)) {
      return;
    }
    this.incidentService.supprimerIncident(incident.id).subscribe({
      next: () => this.router.navigate(['/incidents']),
      error: (erreur: HttpErrorResponse) => this.erreur.set(this.message(erreur, 'Échec de la suppression.')),
    });
  }

  // Après une modification ou une résolution, on recharge le détail :
  // seul GET /api/incidents/{id} renvoie la durée, le niveau d'attention et l'évaluation à jour.
  private charger(id: number): void {
    this.erreur.set(null);
    this.enModification.set(false);
    this.incidentService.obtenirIncidentParId(id).subscribe({
      next: incident => {
        this.incident.set(incident);
        this.choixPrestataire.setValue(incident.prestataireId);
      },
      error: (erreur: HttpErrorResponse) => {
        this.incident.set(null);
        this.erreur.set(this.message(erreur, 'Incident introuvable.'));
      },
    });
    if (this.authService.estAdmin()) {
      this.incidentService.obtenirJournal(id).subscribe({
        next: evenements => this.journal.set(evenements),
        error: (erreur: HttpErrorResponse) => {
          this.journal.set([]);
          this.erreur.set(this.message(erreur, 'Impossible de charger le journal.'));
        },
      });
    }
  }

  private message(erreur: HttpErrorResponse, parDefaut: string): string {
    if (erreur.error?.detail) {
      return erreur.error.detail;
    }
    return erreur.status === HttpStatusCode.Forbidden ? 'Votre profil ne permet pas cette action.' : parDefaut;
  }
}
