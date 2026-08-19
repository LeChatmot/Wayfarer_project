import * as L from 'leaflet';

declare module 'leaflet' {
  class GPX extends L.FeatureGroup {
    constructor(gpx: string, options?: GPXOptions);
    get_distance(): number;
    get_elevation_gain(): number;
    get_elevation_loss(): number;
    get_elevation_min(): number;
    get_elevation_max(): number;
    get_start_time(): Date;
    get_end_time(): Date;
    get_moving_time(): number;
    get_total_time(): number;
    on(event: 'loaded', handler: (e: any) => void): this;
  }

  interface GPXOptions {
    async?: boolean;
    marker_options?: {
      startIconUrl?: string;
      endIconUrl?: string;
      shadowUrl?: string;
    };
    polyline_options?: L.PolylineOptions;
  }

  function gpx(url: string, options?: GPXOptions): GPX;
}
