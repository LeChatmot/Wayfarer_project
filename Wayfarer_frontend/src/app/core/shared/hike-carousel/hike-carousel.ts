import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { Observable } from 'rxjs';
import { HikeCard } from '../hike-card/hike-card';
import { HikeResponse } from '../../models/hike.models';
import { PageResponse } from '../../models/page.model';

const PAGE_SIZE = 5;
const VISIBLE_COUNT = 3;

@Component({
  selector: 'app-hike-carousel',
  templateUrl: './hike-carousel.html',
  styleUrls: ['./hike-carousel.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, MatIconButton, MatIcon, HikeCard],
})
export class HikeCarousel implements OnInit {
  @Input({ required: true })
  loadPage!: (
    page: number,
    size: number
  ) => Observable<PageResponse<HikeResponse>>;

  @Input()
  emptyMessage = 'Aucune randonnée à afficher.';

  @Output()
  readonly openHike = new EventEmitter<HikeResponse>();

  readonly visibleCount = VISIBLE_COUNT;

  items: HikeResponse[] = [];
  startIndex = 0;
  loading = false;
  initialLoading = true;
  hasMore = true;

  private nextPageToLoad = 0;

  constructor(
    private readonly changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.fetchPage();
  }

  get visibleItems(): HikeResponse[] {
    return this.items.slice(
      this.startIndex,
      this.startIndex + this.visibleCount
    );
  }

  get canGoPrev(): boolean {
    return this.startIndex > 0;
  }

  get canGoNext(): boolean {
    return this.startIndex + this.visibleCount < this.items.length;
  }

  prev(): void {
    if (!this.canGoPrev) {
      return;
    }

    this.startIndex--;
    this.changeDetectorRef.markForCheck();
  }

  next(): void {
    if (!this.canGoNext) {
      return;
    }

    this.startIndex++;
    this.maybeLoadMore();
    this.changeDetectorRef.markForCheck();
  }

  onOpen(hike: HikeResponse): void {
    this.openHike.emit(hike);
  }

  private maybeLoadMore(): void {
    if (!this.hasMore || this.loading) {
      return;
    }

    const secondToLastLoadedIndex = this.items.length - 2;
    const lastVisibleIndex =
      this.startIndex + this.visibleCount - 1;

    if (lastVisibleIndex >= secondToLastLoadedIndex) {
      this.fetchPage();
    }
  }

  private fetchPage(): void {
    this.loading = true;

    this.loadPage(this.nextPageToLoad, PAGE_SIZE).subscribe({
      next: (response) => {
        this.items = [
          ...this.items,
          ...response.content,
        ];

        this.hasMore = response.hasNext;
        this.nextPageToLoad++;
        this.loading = false;
        this.initialLoading = false;

        this.changeDetectorRef.markForCheck();
      },
      error: (error) => {
        console.error(error);

        this.loading = false;
        this.initialLoading = false;

        this.changeDetectorRef.markForCheck();
      },
    });
  }
}
