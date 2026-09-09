import { ComponentFixture, TestBed } from '@angular/core/testing';

import HikeSearchFilters from './hike-search-filters';

describe('HikeSearchFilters', () => {
  let component: HikeSearchFilters;
  let fixture: ComponentFixture<HikeSearchFilters>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HikeSearchFilters]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HikeSearchFilters);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
