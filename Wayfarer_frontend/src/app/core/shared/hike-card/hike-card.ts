import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  signal,
} from '@angular/core';
import {Subscription} from 'rxjs';
import {HIKE_DIFFICULTY_LABELS, HikeResponse} from '../../models/hike.models';
import {ThemeService} from '../../services/theme.service';
import {DecimalPipe, NgClass} from '@angular/common';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {environment} from '../../../../environments/environment';
import {HikeService} from '../../services/hike.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-hike-card',
  templateUrl: './hike-card.html',
  styleUrls: ['./hike-card.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    DecimalPipe,
    MatIconButton,
    MatIcon,
    MatButton,
    NgClass,
  ]
})
export class HikeCard implements OnDestroy, OnInit {
  @Input({ required: true }) hike!: HikeResponse;
  @Input() showActions = true;
  @Output() readonly open = new EventEmitter<HikeResponse>();
  @Output() readonly favorite = new EventEmitter<HikeResponse>();


  isDark = false;
  isFavorite = signal(false);
  imageLoaded = false;
  private readonly themeSubscription: Subscription;

  constructor(
    private readonly themeService: ThemeService,
    private readonly changeDetector: ChangeDetectorRef,
    private readonly hikeService: HikeService,
    private readonly router: Router,
  ) {
    this.isDark = this.themeService.isDark;
    this.themeSubscription = this.themeService.isDark$.subscribe((isDark) => {
      this.isDark = isDark;
      this.imageLoaded = false;
      this.changeDetector.markForCheck();
    });
  }
    ngOnInit(): void {
        if(this.hike != null){
          this.isFavorite.set(this.hike.favorite);
        }
    }

  get previewUrl(): string | null {
    return this.isDark
      ? `${environment.staticUrl}` + (this.hike?.previewImageDarkUrl || this.hike?.previewImageLightUrl || null)
      : `${environment.staticUrl}` + (this.hike?.previewImageLightUrl || this.hike?.previewImageDarkUrl || null);
  }

  get distanceKm(): number | null {
    if (this.hike?.distanceMeters != null) return this.hike.distanceMeters/1000;
    return this.hike?.distanceMeters == null ? null : this.hike.distanceMeters / 1000;
  }

  get durationInMinutes(): number | null {
    if (this.hike?.durationSeconds != null) return this.hike.durationSeconds/60;
    return this.hike?.durationSeconds == null ? null : Math.round(this.hike.durationSeconds / 60);
  }

  get difficultyLabel(): string {
    return HIKE_DIFFICULTY_LABELS[this.hike?.difficulty]
  }

  get isLoopRoute(): boolean {
    return this.hike?.backToStart === true;
  }

  formatDuration(minutes: number | null): string {
    if (minutes == null) return '—';
    const hours = Math.floor(minutes / 60);
    const remaining = Math.round(minutes % 60);
    return hours ? `${hours} h${remaining ? ` ${remaining} min` : ''}` : `${remaining} min`;
  }

  onImageLoad(): void { this.imageLoaded = true; }
  onImageError(): void { this.imageLoaded = false; }
  onClickDetail(): void {this.router.navigate(['/hike', this.hike.id])}

  onFavoriteClick(): void {
    if(this.hike.favorite){
      this.hikeService.removeFavorite(this.hike.id).subscribe(() => {
        this.hike.favorite = false;
        this.isFavorite.set(false);
      });
    } else {
      this.hikeService.addFavorite(this.hike.id).subscribe(() => {
        this.hike.favorite = true;
        this.isFavorite.set(true);
      });
    }
  }

  ngOnDestroy(): void { this.themeSubscription.unsubscribe(); }
}
