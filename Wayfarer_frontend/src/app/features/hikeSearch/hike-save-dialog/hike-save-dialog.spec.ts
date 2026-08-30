import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { HikeSaveDialogComponent, HikeSaveDialogData } from './hike-save-dialog';
import { beforeEach, describe, expect, vi, it } from 'vitest';

describe('HikeSaveDialogComponent', () => {
  let component: HikeSaveDialogComponent;
  let fixture: ComponentFixture<HikeSaveDialogComponent>;
  let dialogRefMock: Partial<MatDialogRef<HikeSaveDialogComponent>>;

  let dialogDataMock: HikeSaveDialogData = {
    gpxContent: '<gpx></gpx>',
    preview: {
      distanceMeters: 5000,
      elevationGain: 200,
      elevationLoss: 150,
      durationSeconds: 3600
    } as any
  };

  beforeEach(async () => {
    dialogRefMock = { close: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [HikeSaveDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: dialogRefMock },
        { provide: MAT_DIALOG_DATA, useValue: dialogDataMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(HikeSaveDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form with empty default values', () => {
    expect(component.form.value).toEqual({
      name: '',
      description: '',
      backToStart: false
    });
  });

  it('should expose the injected dialog data', () => {
    expect(component.data).toEqual(dialogDataMock);
  });

  it('should mark the form as invalid when name is empty', () => {
    component.form.patchValue({ name: '' });
    expect(component.form.valid).toBeFalsy();
    expect(component.form.get('name')?.hasError('required')).toBeTruthy();
  });

  it('should mark the form as invalid when name exceeds 60 characters', () => {
    component.form.patchValue({ name: 'a'.repeat(61) });
    expect(component.form.get('name')?.hasError('maxlength')).toBeTruthy();
  });

  it('should mark the form as invalid when description exceeds 2000 characters', () => {
    component.form.patchValue({ name: 'Ma randonnée', description: 'a'.repeat(2001) });
    expect(component.form.get('description')?.hasError('maxlength')).toBeTruthy();
  });

  it('should mark the form as valid with correct values', () => {
    component.form.patchValue({ name: 'Ma randonnée', description: 'Une belle balade' });
    expect(component.form.valid).toBeTruthy();
  });

  it('should close the dialog with form value when form is valid', () => {
    component.form.patchValue({
      name: 'Ma randonnée',
      description: 'Une belle balade',
      backToStart: true
    });

    component.save();

    expect(dialogRefMock.close).toHaveBeenCalledWith({
      name: 'Ma randonnée',
      description: 'Une belle balade',
      backToStart: true
    });
  });

  it('should not close the dialog when form is invalid', () => {
    component.form.patchValue({ name: '' });

    component.save();

    expect(dialogRefMock.close).not.toHaveBeenCalled();
  });
});
