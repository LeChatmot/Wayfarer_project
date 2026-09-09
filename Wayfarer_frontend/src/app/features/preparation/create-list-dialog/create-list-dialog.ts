import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-create-list-dialog',
  standalone: true,
  imports: [ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './create-list-dialog.html',
  styleUrl: './create-list-dialog.scss'
})
export class CreateListDialog {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<CreateListDialog>);

  form = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(120)]]
  });

  submit(): void {
    if (this.form.invalid) return;
    this.dialogRef.close(this.form.controls.name.value);
  }
}
