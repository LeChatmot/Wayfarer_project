import {Component, OnDestroy, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatSliderModule} from '@angular/material/slider';
import * as L from 'leaflet';
import 'leaflet-gpx';
import {MapProvidersService} from '../../../core/services/map-providers.service';
import {catchError, EMPTY, take} from 'rxjs';
import {MapProvidersModel} from '../../../core/models/map-providers.model';
import {Location} from '../../../core/services/location.service';
import {SearchBarLocationComponent} from '../search-bar-location/search-bar-location';
import {createMarkerIcon} from '../../../core/utils/marker.utils';
import {AuthService} from '../../../core/services/auth.service';
import {HikeService} from '../../../core/services/hike.service';
import {MatDialog} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';
import {GpxPreview} from '../../../core/utils/gpx-preview.utils';
import {HikeSaveDialogComponent} from '../../hikeSearch/hike-save-dialog/hike-save-dialog';
import {GpxService} from '../../../core/services/gpx.service';

@Component({
  selector: 'app-map',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatSliderModule,
    SearchBarLocationComponent
  ],
  templateUrl: './map.html',
  styleUrl: './map.scss'
})
export class MapComponent implements OnInit, OnDestroy {
  private map!: L.Map;
  private readonly markersLayer: L.LayerGroup = L.layerGroup();
  private currentMarker?: L.Marker;
  private gpxLayer?: L.Polyline;
  private readonly gpxContent?: string;
  gpxPreview = signal<GpxPreview | null>(null);
  private gpxRawContent = '';
  isAuthenticated = signal<boolean>(false)


  constructor(
    private readonly mapProvidersService: MapProvidersService,
    private readonly authService: AuthService,
    private readonly hikeService: HikeService,
    private readonly gpxService :GpxService,
    private readonly dialog: MatDialog,
    private readonly snackBar: MatSnackBar,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.initMap();
    this.isAuthenticated.set(this.authService.isAuthenticated())
  }

  ngOnDestroy(): void {
    this.map?.remove();
    this.markersLayer.clearLayers();
  }

  private initMap(): void {
    this.map = L.map('map-container', {
      center: [46.5, 2.5],
      zoom: 6,
      zoomControl: false
    });

    L.control.scale({ metric: true, position: 'bottomleft' }).addTo(this.map);

    this.addBaseLayers();
    this.markersLayer.addTo(this.map);
  }

  private addBaseLayers(): void {
    this.mapProvidersService.getMapProvidersList().pipe(
      take(1),
      catchError((err: any) => {
        console.error('Erreur chargement fournisseurs de carte', err);
        this.fallbackToDefaultLayer();
        return EMPTY;
      })
    ).subscribe((providers: MapProvidersModel[]) => {
      if (providers.length === 0) {
        this.fallbackToDefaultLayer();
        return;
      }

      const baseLayers: L.Control.LayersObject = {};
      let defaultLayer: L.TileLayer | null = null;

      providers.forEach(provider => {
        const layer = L.tileLayer(provider.url, {
          attribution: provider.attribution,
          maxZoom: 19,
          crossOrigin: true
        });

        baseLayers[provider.name] = layer;

        if (provider.isDefault) {
          defaultLayer = layer;
        }
      });

      const layerToShow = defaultLayer ?? baseLayers[Object.keys(baseLayers)[0]];
      layerToShow.addTo(this.map);

      L.control.layers(baseLayers, {}, {
        position: 'topright',
        collapsed: false
      }).addTo(this.map);
    });
  }

  private fallbackToDefaultLayer(): void {
    L.tileLayer('https://data.geopf.fr/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=GEOGRAPHICALGRIDSYSTEMS.PLANIGNV2&STYLE=normal&TILEMATRIXSET=PM&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}&FORMAT=image/png', {
      attribution: '&copy; IGN - Géo-portail',
      maxZoom: 19
    }).addTo(this.map);
  }

  onLocationSelected(location: Location): void {
    if (this.currentMarker) {
      this.markersLayer.removeLayer(this.currentMarker);
    }

    this.currentMarker = L.marker(
      [location.latitude, location.longitude],
      {
        icon: createMarkerIcon('#1e4e4b'),
        title: location.name
      }
    ).addTo(this.markersLayer);

    this.map.setView([location.latitude, location.longitude], 12);
    this.currentMarker.bindPopup(`
      <strong>${location.name}</strong><br>
      ${location.type}<br>
      <small>${location.latitude.toFixed(4)}, ${location.longitude.toFixed(4)}</small>
    `).openPopup();
  }

  onGpxFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      return;
    }

    file.text().then(content => {
      this.gpxRawContent = content;
      this.gpxService.preview(file).subscribe({
        next: preview => {
          this.gpxPreview.set(preview);
          this.setPreviewLayer(preview.coordinates);
        },
        error: () => {
          this.snackBar.open('Impossible de lire ce fichier GPX', 'OK', { duration: 3000 });
          this.clearGpx();
        }
      });
    });

    input.value = '';
  }

  private setPreviewLayer(coordinates: [number, number][]): void {
    if (this.gpxLayer) {
      this.map.removeLayer(this.gpxLayer);
    }
    this.gpxLayer = L.polyline(coordinates, {
      color: '#ff5722',
      weight: 4,
      opacity: 0.8
    }).addTo(this.map);
    this.map.fitBounds(this.gpxLayer.getBounds(), { padding: [40, 40] });
  }

  clearGpx(): void {
    if (this.gpxLayer) {
      this.map.removeLayer(this.gpxLayer);
      this.gpxLayer = new L.Polyline([]);
    }
    this.gpxPreview.set(null);
    this.gpxRawContent = '';
  }

  openSaveDialog(): void {
    const preview = this.gpxPreview();
    if (!preview) {
      return;
    }

    const dialogRef = this.dialog.open(HikeSaveDialogComponent, {
      width: '480px',
      maxWidth: '95vw',
      data: { preview }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (!result) {
        return;
      }
      this.gpxService.save({ ...result, gpxContent: this.gpxRawContent }).subscribe({
        next: () => {
          this.snackBar.open('Randonnée enregistrée !', 'OK', { duration: 3000 });
          this.clearGpx();
        },
        error: () => this.snackBar.open('Erreur lors de l\'enregistrement', 'OK', { duration: 3000 })
      });
    });
  }
}
