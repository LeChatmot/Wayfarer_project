import {Component, DestroyRef, inject, OnInit, output} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {HIKE_DIFFICULTY_LABELS, HikeDifficulty, HikeSearchCriteria} from '../../../core/models/hike.models';
import {debounceTime, distinctUntilChanged} from 'rxjs';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {SearchBarLocationComponent} from '../../../core/shared/search-bar-location/search-bar-location';
import {MatFormField, MatLabel} from '@angular/material/input';
import {MatOption, MatSelect} from '@angular/material/select';
import {MatSlideToggle} from '@angular/material/slide-toggle';
import {MatSlider, MatSliderRangeThumb} from '@angular/material/slider';
import {map} from 'rxjs/operators';

export interface DurationStep {
  label: string;
  seconds: number;
}

export const DURATION_STEPS: DurationStep[] = [
  { label: '< 1h', seconds: 0 },
  { label: '1h', seconds: 3600 },
  { label: '2h', seconds: 2 * 3600 },
  { label: '4h', seconds: 4 * 3600 },
  { label: '8h', seconds: 8 * 3600 },
  { label: '12h', seconds: 12 * 3600 },
  { label: '1J', seconds: 24 * 3600 },
  { label: '2J', seconds: 2 * 24 * 3600 },
  { label: '3J', seconds: 3 * 24 * 3600 },
  { label: '4J', seconds: 4 * 24 * 3600 },
  { label: '5J', seconds: 5 * 24 * 3600 },
  { label: '5J+', seconds: Number.MAX_SAFE_INTEGER }
];

export const distanceSteps = [
  { label: '<5 km', kilometers: 0},
  { label: '5 km', kilometers: 5 },
  { label: '10 km', kilometers: 10 },
  { label: '15 km', kilometers: 15 },
  { label: '20 km', kilometers: 20 },
  { label: '25 km', kilometers: 25 },
  { label: '50 km', kilometers: 50 },
  { label: '75 km', kilometers: 75 },
  { label: '100 km', kilometers: 100 },
  { label: '100+ km', kilometers: null }
];

export const elevationSteps = [
  { label: '0 m', meters: 0},
  { label: '1000 m', meters: 1000 },
  { label: '1500 m', meters: 1500 },
  { label: '2000 m', meters: 2000 },
  { label: '2500 m', meters: 2500 },
  { label: '3000 m', meters: 3000 },
  { label: '3500 m', meters: 3500 },
  { label: '4000 m', meters: 4000 },
  { label: '5000+ m', meters: null }
];

@Component({
  selector: 'app-hike-search-filters',
  imports: [
    SearchBarLocationComponent,
    MatFormField,
    MatLabel,
    MatSelect,
    MatOption,
    MatSlideToggle,
    MatSlider,
    ReactiveFormsModule,
    MatSliderRangeThumb
  ],
  templateUrl: './hike-search-filters.html',
  styleUrl: './hike-search-filters.scss',
})
export class HikeSearchFilters implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);

  readonly criteriaChange = output<Partial<HikeSearchCriteria>>();

  readonly difficulties = Object.values(HikeDifficulty);
  readonly difficultyLabels = HIKE_DIFFICULTY_LABELS;
  readonly durationSteps = DURATION_STEPS;
  readonly distanceSteps = distanceSteps;
  readonly elevationSteps = elevationSteps;
  readonly lastDurationIndex = this.durationSteps.length - 1;
  readonly lastDistanceIndex = this.distanceSteps.length - 1;
  readonly lastElevationIndex = this.elevationSteps.length - 1;

  readonly form = this.fb.group({
    latitude: this.fb.control<number | null>(null),
    longitude: this.fb.control<number | null>(null),
    radiusMeters: this.fb.control<number>(20000),
    difficulty: this.fb.control<HikeDifficulty | null>(null),
    backToStart: this.fb.control<boolean>(false),

    distanceMinIndex: this.fb.control<number>(0),
    distanceMaxIndex: this.fb.control<number>(this.lastDistanceIndex),

    elevationGainMinIndex: this.fb.control<number>(0),
    elevationGainMaxIndex: this.fb.control<number>(this.lastElevationIndex),

    elevationLossMinIndex: this.fb.control<number>(0),
    elevationLossMaxIndex: this.fb.control<number>(this.lastElevationIndex),

    durationMinIndex: this.fb.control<number>(0, [
      Validators.min(0)
    ]),

    durationMaxIndex: this.fb.control<number>(
      this.lastDurationIndex,
      [
        Validators.min(0),
        Validators.max(this.lastDurationIndex)
      ]
    )
  });

  ngOnInit(): void {
    this.form.valueChanges
      .pipe(
        debounceTime(500),
        map(value => JSON.stringify(value)),
        distinctUntilChanged(),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(() => {
        this.emitCriteria();
      });
  }

  onLocationSelected(coordinates: {
    latitude: number;
    longitude: number;
  }): void {
    this.form.patchValue({
      latitude: coordinates.latitude,
      longitude: coordinates.longitude
    });
  }

  formatDurationLabel = (index: number): string => {
    return this.durationSteps[index]?.label ?? '';
  };

  formatDistanceLabel = (index: number): string => {
    return this.distanceSteps[index]?.label ?? '';
  };

  formatElevationLabel = (index: number): string => {
    return this.elevationSteps[index]?.label ?? '';
  };

  validateDistanceRange(): void {
    const minControl = this.form.get('distanceMinIndex');
    const maxControl = this.form.get('distanceMaxIndex');

    if (!minControl || !maxControl) {
      return;
    }

    const min = minControl.value ?? 0;
    const max = maxControl.value ?? this.lastDistanceIndex;

    if (min > max) {
      maxControl.setValue(min, { emitEvent: false });
    }
  }

  validateElevationRange(
    type: 'elevationGain' | 'elevationLoss'
  ): void {
    const minControl = this.form.get(`${type}MinIndex`);
    const maxControl = this.form.get(`${type}MaxIndex`);

    if (!minControl || !maxControl) {
      return;
    }

    const min = minControl.value ?? 0;
    const max = maxControl.value ?? this.lastElevationIndex;

    if (min > max) {
      maxControl.setValue(min, { emitEvent: false });
    }
  }

  validateDurationRange(): void {
    const minControl = this.form.get('durationMinIndex');
    const maxControl = this.form.get('durationMaxIndex');

    if (!minControl || !maxControl) {
      return;
    }

    const min = Number(minControl.value);
    const max = Number(maxControl.value);

    if (Number.isNaN(min) || Number.isNaN(max)) {
      return;
    }

    if (min > max) {
      maxControl.setValue(min, {
        emitEvent: false
      });
    }
  }

  private emitCriteria(): void {
    const value = this.form.getRawValue();

    const minDistanceIndex = value.distanceMinIndex ?? 0;
    const maxDistanceIndex = value.distanceMaxIndex ?? this.lastDistanceIndex;

    const minDistance = this.distanceSteps[minDistanceIndex]?.kilometers;
    const maxDistance = this.distanceSteps[maxDistanceIndex]?.kilometers;

    const minElevationGainIndex = value.elevationGainMinIndex ?? 0;
    const maxElevationGainIndex =
      value.elevationGainMaxIndex ?? this.lastElevationIndex;

    const minElevationGain =
      this.elevationSteps[minElevationGainIndex]?.meters ?? undefined;

    const maxElevationGain =
      this.elevationSteps[maxElevationGainIndex]?.meters ?? undefined;

    const minElevationLossIndex = value.elevationLossMinIndex ?? 0;
    const maxElevationLossIndex =
      value.elevationLossMaxIndex ?? this.lastElevationIndex;

    const minElevationLoss =
      this.elevationSteps[minElevationLossIndex]?.meters ?? undefined;

    const maxElevationLoss =
      this.elevationSteps[maxElevationLossIndex]?.meters ?? undefined;

    const minDurationIndex = value.durationMinIndex ?? 0;
    const maxDurationIndex =
      value.durationMaxIndex ?? this.lastDurationIndex;

    this.criteriaChange.emit({
      latitude: value.latitude ?? undefined,
      longitude: value.longitude ?? undefined,
      radiusMeters: value.radiusMeters ?? undefined,
      difficulty: value.difficulty ?? undefined,
      backToStart: value.backToStart || undefined,

      minDistanceMeters: minDistance
        ? minDistance * 1000
        : undefined,

      maxDistanceMeters: maxDistance
        ? maxDistance * 1000
        : undefined,

      minElevationGain,

      maxElevationGain,

      minElevationLoss,

      maxElevationLoss,

      minDurationSeconds:
      this.durationSteps[minDurationIndex]?.seconds,

      maxDurationSeconds:
        maxDurationIndex === this.lastDurationIndex
          ? undefined
          : this.durationSteps[maxDurationIndex]?.seconds
    });
  }
}
