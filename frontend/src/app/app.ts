import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { EnteteComponent } from './layout/entete/entete';
import { EasterEgg } from './components/easter-egg/easter-egg';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, EnteteComponent, EasterEgg],
  templateUrl: './app.html',
})
export class App {}
