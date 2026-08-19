import {Component, signal} from '@angular/core';
import {provideNativeDateAdapter} from '@angular/material/core';
import {RouterOutlet} from '@angular/router';
import {Header} from './core/shared/header/header';

@Component({
  selector: 'app-root',
  providers: [provideNativeDateAdapter()],
  imports: [
    RouterOutlet,
    Header
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('Wayfarer_frontend');
}
