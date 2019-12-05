import { Component, OnInit, Inject } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { DialogData } from '../experiments/experiments.component';

@Component({
  selector: 'app-experiments-mgmt-dialog',
  templateUrl: './experiments-mgmt-dialog.component.html',
  styleUrls: ['./experiments-mgmt-dialog.component.css']
})
export class ExperimentsMgmtDialogComponent implements OnInit {

  expMgmtForm: FormGroup;

  constructor(
    private _formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<ExperimentsMgmtDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) {}

  ngOnInit(): void {
    this.expMgmtForm = this._formBuilder.group({
      selectedStatus: ['', Validators.required]
    });
  } 

  onNoClick(): void {
    this.dialogRef.close();
  }

  confirmSelection(): void {
    this.dialogRef.close(this.expMgmtForm.get('selectedStatus').value);
  }
}
