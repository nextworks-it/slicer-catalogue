import { Component, OnInit, Inject } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { ExperimentsMgmtDialogComponent } from '../experiments-mgmt-dialog/experiments-mgmt-dialog.component';
import { DialogData } from '../experiments/experiments.component';

@Component({
  selector: 'app-experiments-results-dialog',
  templateUrl: './experiments-results-dialog.component.html',
  styleUrls: ['./experiments-results-dialog.component.css']
})
export class ExperimentsResultsDialogComponent implements OnInit {

  expResultsForm: FormGroup;

  constructor(
    private _formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<ExperimentsMgmtDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) {}

  ngOnInit(): void {
    this.expResultsForm = this._formBuilder.group({
      selectedExecution: ['', Validators.required]
    });
  } 

  onNoClick(): void {
    this.dialogRef.close();
  }

  confirmSelection(): void {
    this.dialogRef.close(this.expResultsForm.get('selectedExecution').value);
  }
}
