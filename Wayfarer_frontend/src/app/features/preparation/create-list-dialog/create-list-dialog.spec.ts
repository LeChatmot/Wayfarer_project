import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateListDialog } from './create-list-dialog';

describe('CreateListDialog', () => {
  let component: CreateListDialog;
  let fixture: ComponentFixture<CreateListDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateListDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateListDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
