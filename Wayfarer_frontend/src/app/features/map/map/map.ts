import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSliderModule } from '@angular/material/slider';
import * as L from 'leaflet';
import 'leaflet-gpx';
import { MapProvidersService } from '../../../core/services/map-providers.service';
import { catchError, EMPTY, take } from 'rxjs';
import { MapProvidersModel } from '../../../core/models/map-providers.model';
import { Location} from '../../../core/services/location.service';
import { SearchBarLocationComponent } from '../search-bar-location/search-bar-location';
import {createMarkerIcon} from '../../../core/utils/marker.utils';

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

  constructor(
    private readonly mapProvidersService: MapProvidersService,
  ) {}

  ngOnInit(): void {
    this.initMap();
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

    L.control.zoom({ position: 'bottomright' }).addTo(this.map);
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
}
