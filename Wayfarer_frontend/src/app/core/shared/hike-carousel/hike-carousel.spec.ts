import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HikeCarousel } from './hike-carousel';

describe('HikeCarousel', () => {
  let component: HikeCarousel;
  let fixture: ComponentFixture<HikeCarousel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HikeCarousel]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HikeCarousel);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
