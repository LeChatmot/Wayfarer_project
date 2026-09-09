import { Injectable, NgZone, OnDestroy } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ThemeService implements OnDestroy {
  private readonly darkModeSubject = new BehaviorSubject<boolean>(this.readInitialTheme());
  readonly isDark$: Observable<boolean> = this.darkModeSubject.asObservable();

  private mediaQuery?: MediaQueryList;
  private readonly onThemeChange = (event: MediaQueryListEvent): void => {
    this.darkModeSubject.next(event.matches);
  };

  constructor(private readonly zone: NgZone) {
    if (typeof window !== 'undefined' && typeof window.matchMedia === 'function') {
      this.mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
      this.zone.runOutsideAngular(() => {
        this.mediaQuery?.addEventListener('change', this.onThemeChange);
      });
    }
  }

  get isDark(): boolean {
    return this.darkModeSubject.value;
  }

  private readInitialTheme(): boolean {
    return typeof window !== 'undefined'
      && typeof window.matchMedia === 'function'
      && window.matchMedia('(prefers-color-scheme: dark)').matches;
  }

  ngOnDestroy(): void {
    this.mediaQuery?.removeEventListener('change', this.onThemeChange);
  }
}
