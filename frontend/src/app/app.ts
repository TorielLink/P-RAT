import { Component, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { EquipementService } from './core/equipement.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, AsyncPipe],
  templateUrl: './app.html'
})
export class App {
  protected readonly equipements$ = inject(EquipementService).getAll();
}
