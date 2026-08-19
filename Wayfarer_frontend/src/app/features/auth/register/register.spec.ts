import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterComponent } from './register';
import mockLocalStorage from '../../../core/mock/mockLocalStorage';
import {LoginComponent} from '../login/login';
import {provideRouter} from '@angular/router';
import {LOCAL_STORAGE} from '../../../core/tokens/local-storage.token';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [provideRouter([]),
        { provide: LOCAL_STORAGE, useValue: mockLocalStorage },],
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
