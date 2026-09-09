import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import {Item} from '../../../core/models/item-list.model';

@Component({
  selector: 'app-add-item-dialog',
  standalone: true,
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './add-item-dialog.html',
  styleUrl: './add-item-dialog.scss'
})
export class AddItemDialog {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<AddItemDialog>);
  data = inject<Item | null>(MAT_DIALOG_DATA, { optional: true });

  form = this.fb.group({
    name: [this.data?.name ?? '', [Validators.required, Validators.maxLength(60)]],
    quantity: [this.data?.quantity ?? null as number | null, [Validators.min(0)]]
  });

  submit(): void {
    if (this.form.invalid) return;
    this.dialogRef.close(this.form.getRawValue());
  }
}
