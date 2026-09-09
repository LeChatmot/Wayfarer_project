import {ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import {HikeSearchFilters} from '../hike-search-filters/hike-search-filters';
import {HikeCard} from '../../../core/shared/hike-card/hike-card';
import {HikeService} from '../../../core/services/hike.service';
import {ThemeService} from '../../../core/services/theme.service';
import {HikeResponse, HikeSearchCriteria} from '../../../core/models/hike.models';
import {PageResponse} from '../../../core/models/page.model';

@Component({
  selector: 'app-hike-search',
  standalone: true,
  imports: [
    CommonModule,
    HikeSearchFilters,
    HikeCard,
    MatProgressSpinnerModule
  ],
  templateUrl: './hike-search.html',
  styleUrl: './hike-search.scss',
})
export class HikeSearch implements OnInit {
  private readonly hikeService = inject(HikeService);
  protected readonly themeService = inject(ThemeService);

  hikes: HikeResponse[] = [];
  isLoading = false;
  hasMorePages = true;
  currentPage = 0;
  currentCriteria?: HikeSearchCriteria;

  constructor(private readonly cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.setupInfiniteScroll();
    this.currentCriteria = {};
    this.loadMoreHikes();
  }

  onSearchCriteriaChange(criteria: Partial<HikeSearchCriteria>): void {
    this.currentCriteria = criteria as HikeSearchCriteria;
    this.hikes = [];
    this.currentPage = 0;
    this.hasMorePages = true;
    this.loadMoreHikes();
  }

  private loadMoreHikes(): void {
    if (!this.currentCriteria || this.isLoading || !this.hasMorePages) {
      return;
    }

    this.isLoading = true;
    const criteriaWithPage = {
      ...this.currentCriteria,
      page: this.currentPage
    };

    this.hikeService.search(criteriaWithPage).subscribe({
      next: (response: PageResponse<HikeResponse>) => {
        this.hikes = [...this.hikes, ...response.content];
        this.hasMorePages = !response.last;
        this.currentPage++;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  private setupInfiniteScroll(): void {
    const scrollContainer = document.querySelector('.hikes-grid-container');
    if (!scrollContainer) return;

    scrollContainer.addEventListener('scroll', () => {
      const { scrollTop, scrollHeight, clientHeight } = scrollContainer as HTMLElement;
      const threshold = scrollHeight - clientHeight - 500;

      if (scrollTop >= threshold && !this.isLoading && this.hasMorePages) {
        this.loadMoreHikes();
      }
    });
  }
}
