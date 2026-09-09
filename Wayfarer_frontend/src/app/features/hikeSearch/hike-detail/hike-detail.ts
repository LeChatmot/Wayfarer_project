import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import * as L from 'leaflet';
import 'leaflet-gpx';
import { HikeService } from '../../../core/services/hike.service';
import { AuthService } from '../../../core/services/auth.service';
import { HIKE_DIFFICULTY_LABELS, HikePathResponse, HikeResponse } from '../../../core/models/hike.models';
import {forkJoin} from 'rxjs';

@Component({
  selector: 'app-hike-detail',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule
  ],
  templateUrl: './hike-detail.html',
  styleUrl: './hike-detail.scss'
})
export class HikeDetail implements OnInit, OnDestroy {

  hike = signal<HikeResponse | null>(null);
  isEditing = signal(false);
  canManage = signal(false);

  readonly difficultyLabels = HIKE_DIFFICULTY_LABELS;

  private map: L.Map | null = null;
  private gpxLayer: any = null;

  editForm: FormGroup;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly fb: FormBuilder,
    private readonly hikeService: HikeService,
    private readonly authService: AuthService,
    private readonly snackBar: MatSnackBar
  ) {
    this.editForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(2000)]]
    });
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.hikeService.getById(id).subscribe({
      next: (hike) => {
        this.hike.set(hike);
        this.checkPermissions(hike);
        setTimeout(() => this.initMap(hike));
      },
      error: () => this.router.navigate(['/'])
    });
  }

  ngOnDestroy(): void {
    this.map?.remove();
  }

  private checkPermissions(hike: HikeResponse): void {
    forkJoin({
      isAdmin: this.authService.isAdmin(),
      isCreator: this.hikeService.isCreator(hike.id)
    }).subscribe({
      next: ({ isAdmin, isCreator }) => {
        this.canManage.set(isAdmin || isCreator);
      },
      error: () => this.canManage.set(false)
    });
  }

  private initMap(hike: HikeResponse): void {
    this.map = L.map('hike-detail-map');

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OSM - WMTS',
      maxZoom: 19
    }).addTo(this.map);

    this.hikeService.getHikePath(hike.id).subscribe({
      next: (hikePath) => this.displayTrace(hike, hikePath),
      error: () => {
        this.map?.setView([hike.startingPoint.lat, hike.startingPoint.lng], 13);
      }
    });
  }

  private displayTrace(hike: HikeResponse, hikePath: HikePathResponse): void {
    if (!this.map) {
      return;
    }

    const latLngs = hikePath.path.map(([lat, lng, ele]) => [lat, lng, ele] as L.LatLngTuple);

    if (latLngs.length === 0) {
      this.map.setView([hike.startingPoint.lat, hike.startingPoint.lng], 13);
      return;
    }

    const trace = L.polyline(latLngs, {
      color: '#1e4e4b',
      weight: 5,
      opacity: 0.85
    }).addTo(this.map);

    const start = latLngs[0];
    const end = latLngs[latLngs.length - 1];

    L.circleMarker(start, {
      radius: 8,
      color: '#2e7d32',
      fillColor: '#2e7d32',
      fillOpacity: 1
    }).addTo(this.map).bindTooltip('Départ');

    if (!hike.backToStart) {
      L.circleMarker(end, {
        radius: 8,
        color: '#c62828',
        fillColor: '#c62828',
        fillOpacity: 1
      }).addTo(this.map).bindTooltip('Arrivée');
    }

    this.map.fitBounds(trace.getBounds(), { padding: [30, 30] });
  }

  formatDuration(durationSeconds: number | null | undefined): string {
    if (durationSeconds == null) {
      return '—';
    }
    const totalMinutes = Math.round(durationSeconds / 60);
    const hours = Math.floor(totalMinutes / 60);
    const minutes = totalMinutes % 60;
    return hours > 0 ? `${hours} h ${minutes.toString().padStart(2, '0')}` : `${minutes} min`;
  }

  startEdit(): void {
    const hike = this.hike();
    if (!hike) {
      return;
    }
    this.editForm.setValue({
      name: hike.name,
      description: hike.description ?? ''
    });
    this.isEditing.set(true);
  }

  cancelEdit(): void {
    this.isEditing.set(false);
  }

  saveEdit(): void {
    if (this.editForm.invalid) {
      return;
    }
    const hike = this.hike();
    if (!hike) {
      return;
    }

    const request = {
      name: this.editForm.value.name!,
      description: this.editForm.value.description ?? ''
    };

    this.hikeService.update(hike.id, request).subscribe({
      next: (updatedHike) => {
        this.hike.set(updatedHike);
        this.isEditing.set(false);
        this.snackBar.open('Randonnée mise à jour !', 'OK', { duration: 3000 });
      },
      error: () => this.snackBar.open('Erreur lors de la mise à jour', 'OK', { duration: 3000 })
    });
  }

  deleteHike(): void {
    const hike = this.hike();
    if (!hike) {
      return;
    }
    if (!confirm(`Supprimer définitivement "${hike.name}" ?`)) {
      return;
    }
    this.hikeService.delete(hike.id).subscribe({
      next: () => {
        this.snackBar.open('Randonnée supprimée', 'OK', { duration: 3000 });
        this.router.navigate(['/map']);
      },
      error: () => this.snackBar.open('Erreur lors de la suppression', 'OK', { duration: 3000 })
    });
  }
}
