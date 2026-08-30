import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HikeSearch } from './hike-search';

describe('HikeSearch', () => {
  let component: HikeSearch;
  let fixture: ComponentFixture<HikeSearch>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HikeSearch]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HikeSearch);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
