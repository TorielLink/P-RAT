import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { EnteteComponent } from './layout/entete/entete';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, EnteteComponent],
  templateUrl: './app.html',
})
export class App {}
