export interface GpxPreview {
  coordinates: [number, number][];
  distanceMeters: number;
  elevationGain: number;
  elevationLoss: number;
}

export interface HikeSaveRequest {
  name: string;
  description: string;
  backToStart: boolean;
  gpxContent: string;
}

export interface HikeResponse {
  id: number;
  name: string;
  description: string;
  backToStart: boolean;
  distanceMeters: number;
  elevationGain: number;
  elevationLoss: number;
  startingPoint: { lat: number; lng: number; alt: number };
  createdBy: string;
  createdAt: string;
}
