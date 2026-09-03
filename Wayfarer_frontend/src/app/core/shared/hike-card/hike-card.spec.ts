import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HikeCard } from './hike-card';

describe('HikeCard', () => {
  let component: HikeCard;
  let fixture: ComponentFixture<HikeCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HikeCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HikeCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
