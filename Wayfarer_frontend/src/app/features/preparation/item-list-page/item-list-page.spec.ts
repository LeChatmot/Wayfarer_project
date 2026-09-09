import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItemListPage } from './item-list-page';

describe('ItemListPage', () => {
  let component: ItemListPage;
  let fixture: ComponentFixture<ItemListPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ItemListPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ItemListPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
