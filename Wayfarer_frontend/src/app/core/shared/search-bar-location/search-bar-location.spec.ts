import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchBarLocationComponent } from './search-bar-location';

describe('SearchBarLocationComponent', () => {
  let component: SearchBarLocationComponent;
  let fixture: ComponentFixture<SearchBarLocationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SearchBarLocationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchBarLocationComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
