import { Routes } from '@angular/router';
import {LoginComponent} from './features/auth/login/login';
import {RegisterComponent} from './features/auth/register/register';
import { MapComponent } from './features/map/map/map';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'map', component: MapComponent },
  { path: '', redirectTo: '/map', pathMatch: 'full' },
];
