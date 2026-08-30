import { Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogActions, MatDialogClose, MatDialogContent, MatDialogRef, MatDialogTitle} from '@angular/material/dialog';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatCheckbox} from '@angular/material/checkbox';
import {DecimalPipe} from '@angular/common';
import {GpxPreview} from '../../../core/utils/gpx-preview.utils';
import {MatIcon} from '@angular/material/icon';
import {MatButton} from '@angular/material/button';

export interface HikeSaveDialogData {
  gpxContent: string;
  preview: GpxPreview;
}

export interface HikeSaveDialogResult {
  name: string;
  description: string;
  backToStart: boolean;
}

@Component({
  selector: 'app-hike-save-dialog',
  imports: [
    MatDialogContent,
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatError,
    MatCheckbox,
    MatDialogActions,
    DecimalPipe,
    MatDialogClose,
    MatIcon,
    MatInput,
    MatButton,
    MatDialogTitle
  ],
  templateUrl: './hike-save-dialog.html',
  styleUrl: './hike-save-dialog.scss',
})
export class HikeSaveDialogComponent {
  form: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly dialogRef: MatDialogRef<HikeSaveDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public readonly data: HikeSaveDialogData
  ) {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(60)]],
      description: ['', Validators.maxLength(2000)],
      backToStart: [false]
    });
  }

  save(): void {
    if (this.form.valid) {
      this.dialogRef.close(this.form.value as HikeSaveDialogResult);
    }
  }
}
