import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginComponent } from './login';

import mockLocalStorage from '../../../core/mock/mockLocalStorage';
import {provideRouter} from '@angular/router';
import {LOCAL_STORAGE} from '../../../core/tokens/local-storage.token';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [provideRouter([]),
        { provide: LOCAL_STORAGE, useValue: mockLocalStorage },],
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
