import { ChangeDetectorRef, Component, EventEmitter, Output } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, switchMap, of } from 'rxjs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { LocationService, Location } from '../../services/location.service';
import {MatIconButton} from '@angular/material/button';

@Component({
  selector: 'app-search-bar-location',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatIconButton
  ],
  templateUrl: './search-bar-location.html',
  styleUrl: './search-bar-location.scss'
})
export class SearchBarLocationComponent {
  @Output() locationSelected = new EventEmitter<Location>();

  searchControl = new FormControl('');
  results: Location[] = [];
  isLoading = false;
  error: string | null = null;
  hasResults = false;

  constructor(
    private readonly locationService: LocationService,
    private readonly cdr: ChangeDetectorRef
  ) {
    this.setupSearch();
  }

  private setupSearch(): void {
    this.searchControl.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(query => {
          if (!query || query.length < 2) {
            this.results = [];
            this.hasResults = false;
            this.cdr.markForCheck();
            return of([]);
          }
          this.isLoading = true;
          this.error = null;
          this.cdr.markForCheck();
          return this.locationService.searchLocations(query);
        })
      )
      .subscribe({
        next: (results) => {
          this.results = results;
          this.hasResults = results.length > 0;
          this.isLoading = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.error = 'Erreur lors de la recherche';
          this.hasResults = false;
          this.isLoading = false;
          console.error('Search error:', err);
          this.cdr.markForCheck();
        }
      });
  }

  onSearch(): void {
    const query = this.searchControl.value;
    if (!query || query.length < 2) return;

    this.isLoading = true;
    this.error = null;

    this.locationService.searchLocations(query).subscribe({
      next: (results) => {
        this.results = results;
        this.hasResults = results.length > 0;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.error = 'Aucun résultat trouvé';
        this.hasResults = false;
        this.isLoading = false;
        console.error('Search error:', err);
        this.cdr.markForCheck();
      }
    });
  }

  selectLocation(location: Location): void {
    this.locationSelected.emit(location);
    this.results = [];
    this.hasResults = false;
    this.searchControl.setValue('');
  }
}
