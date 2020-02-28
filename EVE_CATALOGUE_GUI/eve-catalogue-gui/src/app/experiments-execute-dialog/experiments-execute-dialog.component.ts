import { Component, OnInit, Inject } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { ExperimentsMgmtDialogComponent } from '../experiments-mgmt-dialog/experiments-mgmt-dialog.component';
import { DialogData } from '../experiments/experiments.component';

@Component({
  selector: 'app-experiments-execute-dialog',
  templateUrl: './experiments-execute-dialog.component.html',
  styleUrls: ['./experiments-execute-dialog.component.css']
})
export class ExperimentsExecuteDialogComponent implements OnInit {

  expExecForm: FormGroup;

  constructor(
    private _formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<ExperimentsMgmtDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) {}

  ngOnInit(): void {
    this.expExecForm = this._formBuilder.group({
      selectedAction: ['', Validators.required],
      executionName: ['']
    });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }


  confirmSelection(): void {
    this.dialogRef.close(this.expExecForm);
  }

}
