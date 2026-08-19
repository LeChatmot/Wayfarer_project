import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchBarLocation } from './search-bar-location';

describe('SearchBarLocation', () => {
  let component: SearchBarLocation;
  let fixture: ComponentFixture<SearchBarLocation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SearchBarLocation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchBarLocation);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
