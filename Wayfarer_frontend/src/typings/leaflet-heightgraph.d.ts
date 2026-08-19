import * as L from 'leaflet';

declare module 'leaflet' {
  namespace Control {
    class Heightgraph extends L.Control {
      constructor(options?: HeightgraphOptions);
      addData(geojson: GeoJSON.FeatureCollection): void;
      onAdd(map: L.Map): HTMLElement;
    }
  }

  namespace control {
    function heightgraph(options?: HeightgraphOptions): Control.Heightgraph;
  }

  interface HeightgraphOptions {
    width?: number;
    height?: number;
    margins?: { top: number; right: number; bottom: number; left: number };
    expandControls?: boolean;
    expand?: 'summary' | 'chart' | false;
    mappings?: Record<string, { text: string; color: string }>;
  }
}
