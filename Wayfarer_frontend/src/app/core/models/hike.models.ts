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

export interface HikeUpdateRequest {
  name: string;
  description: string;
}

export interface HikePathResponse {
  path: [number, number, number][];
}

export interface HikeResponse {
  id: number;
  name: string;
  description: string;
  backToStart: boolean;
  distanceMeters: number;
  elevationGain: number;
  elevationLoss: number;
  startingPoint: {
    lat: number;
    lng: number;
    alt: number;
  };
  startingPointName: string,
  createdBy: string;
  durationSeconds: number;
  difficulty: HikeDifficulty;
  previewImageLightUrl: string;
  previewImageDarkUrl: string;
  favorite: boolean;
}

export enum HikeDifficulty {
  EASY = 'EASY',
  MEDIUM = 'MEDIUM',
  HARD = 'HARD',
  VERY_HARD = 'VERY_HARD'
}

export const HIKE_DIFFICULTY_LABELS: Record<HikeDifficulty, string> = {
  [HikeDifficulty.EASY]: 'Facile',
  [HikeDifficulty.MEDIUM]: 'Moyen',
  [HikeDifficulty.HARD]: 'Difficile',
  [HikeDifficulty.VERY_HARD]: 'Très difficile'
};

export interface HikeSearchCriteria {
  latitude?: number;
  longitude?: number;
  radiusMeters?: number;
  difficulty?: HikeDifficulty;
  backToStart?: boolean;
  minDistanceMeters?: number;
  maxDistanceMeters?: number;
  minElevationGain?: number;
  maxElevationGain?: number;
  minElevationLoss?: number;
  maxElevationLoss?: number;
  minDurationSeconds?: number;
  maxDurationSeconds?: number;
  page?: number;
  size?: number;
  sort?: string;
}
