import {Component, Input, OnInit} from '@angular/core';
import * as L from 'leaflet';
import {LatLngExpression} from 'leaflet';
import 'leaflet-gpx';
import {MapProvidersModel} from '../../models/map-providers.model';
import {HikeResponse} from '../../models/hike.models';

@Component({
  selector: 'app-hike-card',
  imports: [],
  templateUrl: './hike-card.html',
  styleUrl: './hike-card.scss',
})
export class HikeCard implements OnInit{
  private map!: L.Map;

  @Input({ required: true}) mapProviderIgn: MapProvidersModel | undefined;
  @Input({ required: true}) hike: HikeResponse | undefined;

  ngOnInit(): void {
    this.initMap();
  }

  private initMap(): void {
    this.map = L.map('map-container', {
      center: [46.5, 2.5],
      zoom: 6,
      zoomControl: false
    });

    if (this.mapProviderIgn) {
      L.tileLayer(this.mapProviderIgn.url, {
        attribution: this.mapProviderIgn.attribution,
        maxZoom: 19
      }).addTo(this.map);

      if (this.hike){
        this.map.setView(this.hike.startingPoint as LatLngExpression);
      }
    }
  }

}
