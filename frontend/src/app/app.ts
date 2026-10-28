import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Equipements } from './equipements';
import {EnteteComponent} from './layout/entete/entete';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Equipements, EnteteComponent],
  templateUrl: './app.html',
})
export class App {}
