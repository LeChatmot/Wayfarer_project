import { Component } from '@angular/core';
import {HikeCard} from '../../../core/shared/hike-card/hike-card';
import {MapProvidersModel} from '../../../core/models/map-providers.model';
import {HttpClient} from '@angular/common/http';
import {MapProvidersService} from '../../../core/services/map-providers.service';

@Component({
  selector: 'app-hike-search',
  imports: [
    HikeCard
  ],
  templateUrl: './hike-search.html',
  styleUrl: './hike-search.scss',
})
export class HikeSearch {

  IGN_MAP_PROVIDER: MapProvidersModel | undefined;

  constructor(private mapProviderService: MapProvidersService) {
    mapProviderService.getPlanIGNMapProvier().subscribe(ignMapProvider => this.IGN_MAP_PROVIDER = ignMapProvider);
  }

}
