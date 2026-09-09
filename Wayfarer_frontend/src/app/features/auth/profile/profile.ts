import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { HikeCarousel } from '../../../core/shared/hike-carousel/hike-carousel';
import { HikeService } from '../../../core/services/hike.service';
import { UserService } from '../../../core/services/user.service';
import { HikeResponse } from '../../../core/models/hike.models';
import { PageResponse } from '../../../core/models/page.model';
import { UserResponse } from '../../../core/models/user.model';
import {MatSnackBar} from '@angular/material/snack-bar';
import {AuthService} from '../../../core/services/auth.service';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';

type ProfileMode = 'view' | 'editing' | 'saving';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, ReactiveFormsModule, MatButton, HikeCarousel, MatFormField, MatLabel, MatInput, MatError],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Profile implements OnInit {
  user = signal<UserResponse | null>(null);
  mode = signal<ProfileMode>('view');
  errorMessage = signal<string | null>(null);

  readonly form: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly hikeService: HikeService,
    private readonly userService: UserService,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly snackBar: MatSnackBar
  ) {
    this.form = this.fb.group({
                  username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(60)]],
                  email: ['', [Validators.required, Validators.email]],
                });
  }

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe((u) => this.user.set(u));
  }

  readonly loadFavorites = (page: number, size: number): Observable<PageResponse<HikeResponse>> =>
    this.hikeService.getFavorites(page, size);

  readonly loadMyHikes = (page: number, size: number): Observable<PageResponse<HikeResponse>> =>
    this.hikeService.getMyHikes(page, size);

  onOpenHike(hike: HikeResponse): void {
    this.router.navigate(['/hikes', hike.id]);
  }

  startEditing(): void {
    const current = this.user();
    if (!current) return;
    this.form.setValue({ username: current.username, email: current.email });
    this.errorMessage.set(null);
    this.mode.set('editing');
  }

  deleteAccount(): void{
    if (!confirm(`Supprimer définitivement votre compte ?`)) {
      return;
    }
    this.userService.deleteCurrentUser().subscribe({
      next: () => {
        this.snackBar.open('Compte supprimé', 'OK', { duration: 3000 });
        this.authService.logout().subscribe(value => {
          console.log("Compte supprimé")
        })
        this.router.navigate(['/map']);
      },
      error: () => this.snackBar.open('Erreur lors de la suppression', 'OK', { duration: 3000 })
    });
  }

  cancelEditing(): void {
    this.form.reset();
    this.errorMessage.set(null);
    this.mode.set('view');
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.mode.set('saving');
    this.errorMessage.set(null);

    this.userService.updateCurrentUser(this.form.getRawValue()).subscribe({
      next: (updated) => {
        this.user.set(updated);
        this.mode.set('view');
      },
      error: () => {
        this.errorMessage.set("Impossible d'enregistrer les modifications.");
        this.mode.set('editing');
      },
    });
  }
}
