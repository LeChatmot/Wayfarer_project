export interface GpxPreview {
  coordinates: [number, number][];
  distanceMeters: number;
  elevationGain: number;
  elevationLoss: number;
}

export function parseGpxPreview(gpxContent: string): GpxPreview | null {
  const doc = new DOMParser().parseFromString(gpxContent, 'application/xml');
  if (doc.querySelector('parsererror')) return null;

  const trkpts = Array.from(doc.getElementsByTagName('trkpt'));
  if (trkpts.length < 2) return null;

  let distance = 0, gain = 0, loss = 0, prevEle: number | null = null;
  const coordinates: [number, number][] = [];

  for (const pt of trkpts) {
    const lat = parseFloat(pt.getAttribute('lat') ?? '');
    const lon = parseFloat(pt.getAttribute('lon') ?? '');
    if (isNaN(lat) || isNaN(lon)) continue;

    const eleText = pt.getElementsByTagName('ele')[0]?.textContent;
    const ele = eleText ? parseFloat(eleText) : null;

    const prev = coordinates[coordinates.length - 1];
    if (prev) {
      distance += haversine(prev[0], prev[1], lat, lon);
      if (ele !== null && prevEle !== null) {
        const dz = ele - prevEle;
        if (Math.abs(dz) >= 3) {
          if (dz > 0) gain += dz; else loss += Math.abs(dz);
        }
      }
    }
    if (ele !== null) prevEle = ele;
    coordinates.push([lat, lon]);
  }

  return coordinates.length >= 2 ? { coordinates, distanceMeters: distance, elevationGain: gain, elevationLoss: loss } : null;
}

function haversine(lat1: number, lon1: number, lat2: number, lon2: number): number {
  const r = 6371000;
  const dLat = (lat2 - lat1) * Math.PI / 180;
  const dLon = (lon2 - lon1) * Math.PI / 180;
  const h = Math.sin(dLat / 2) ** 2
    + Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2) ** 2;
  return 2 * r * Math.asin(Math.sqrt(h));
}
