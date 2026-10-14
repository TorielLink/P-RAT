import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Equipements } from './equipements';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Equipements],
  templateUrl: './app.html',
})
export class App {}
