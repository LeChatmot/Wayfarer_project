import { Routes } from '@angular/router';
import {LoginComponent} from './features/auth/login/login';
import {RegisterComponent} from './features/auth/register/register';
import { MapComponent } from './features/map/map/map';
import {HikeSearch} from './features/hikeSearch/hike-search/hike-search';
import {HikeDetail} from './features/hikeSearch/hike-detail/hike-detail';
import {Profile} from './features/auth/profile/profile';
import {ItemListsPage} from './features/preparation/item-list-page/item-list-page';
import {authGuard} from './core/guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'map', component: MapComponent },
  { path: 'search', component: HikeSearch },
  { path: 'hike/:id', component: HikeDetail },
  { path: 'profile', component: Profile, canActivate: [authGuard]},
  { path: 'preparation', component: ItemListsPage, canActivate: [authGuard] },
  { path: '', redirectTo: '/map', pathMatch: 'full' },
];
