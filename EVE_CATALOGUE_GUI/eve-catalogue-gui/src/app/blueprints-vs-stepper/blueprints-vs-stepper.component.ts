import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, FormControl, Validators } from '@angular/forms';
import { NumberSymbol } from '@angular/common';

@Component({
  selector: 'app-blueprints-vs-stepper',
  templateUrl: './blueprints-vs-stepper.component.html',
  styleUrls: ['./blueprints-vs-stepper.component.css']
})
export class BlueprintsVsStepperComponent implements OnInit {

  isLinear = false;
  isButtonVisible = false;
  arrayItems: {
    paramId: string;
    minValId: string;
    maxValId: string;
  }[];
  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;
  thirdFormGroup: FormGroup;

  constructor(private _formBuilder: FormBuilder) {
    this.thirdFormGroup = this._formBuilder.group({
      formArray: this._formBuilder.array([]),
    });
  }

  ngOnInit() {

    this.arrayItems = [
      { paramId: 'paramId_0', minValId: 'minValId_0', maxValId: 'maxVal_0' }
    ];
    this.firstFormGroup = this._formBuilder.group({
      firstCtrl: ['', Validators.required]
    });
    this.secondFormGroup = this._formBuilder.group({
      secondCtrl: ['', Validators.required]
    });
    /*this.thirdFormGroup = this._formBuilder.group({
      thirdCtrl: ['', Validators.required]
    });*/
  }

  get formArray() {
    return this.thirdFormGroup.get('formArray') as FormArray;
  }

  addParamsRow() {
    console.log("HERE!!!");
    var num = this.formArray.length - 1;
    var item = {
      paramId: 'paramId_' + num, 
      minValId: 'minValId_' + num, 
      maxValId: 'maxVal_' + num
    };
    this.addItem(item);
  }

  removeItem() {
    this.arrayItems.pop();
    this.formArray.removeAt(this.formArray.length - 1);
  }

  addItem(item) {
    this.arrayItems.push(item);
    this.formArray.push(this._formBuilder.control(false));
  }
}
