import { Component, inject } from '@angular/core';
import {AuthService} from '../../core/auth/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-entete',
  templateUrl: './entete.html',
  styleUrl: './entete.scss',
})
export class EnteteComponent {
  protected readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  seDeconnecter(): void {
    this.authService.deconnecter().subscribe({
      complete: () => this.router.navigate(['/login']),
    });
  }
}
