import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, FormControl, Validators } from '@angular/forms';
import { NumberSymbol, DOCUMENT } from '@angular/common';

@Component({
  selector: 'app-blueprints-vs-stepper',
  templateUrl: './blueprints-vs-stepper.component.html',
  styleUrls: ['./blueprints-vs-stepper.component.css']
})
export class BlueprintsVsStepperComponent implements OnInit {

  isLinear = false;
  isButtonVisible = false;
  /*arrayItems: {
    paramId: string;
    minValId: string;
    maxValId: string;
  }[];*/
  items: FormArray;
  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;
  thirdFormGroup: FormGroup;

  constructor(@Inject(DOCUMENT) document, private _formBuilder: FormBuilder) {
    /*this.thirdFormGroup = this._formBuilder.group({
      formArray: this._formBuilder.array([]),
    });*/
  }

  ngOnInit() {
    //this.arrayItems = [];
    this.firstFormGroup = this._formBuilder.group({
      firstCtrl: ['', Validators.required]
    });
    this.secondFormGroup = this._formBuilder.group({
      secondCtrl: ['', Validators.required]
    });
    this.thirdFormGroup = this._formBuilder.group({
      nsdId: ['', Validators.required],
      nsdVersion: ['', Validators.required],
      nsFlavourId: ['', Validators.required],
      nsInstLevel: ['', Validators.required],
      items: this._formBuilder.array([this.createItem()])
    });
    //this.addItem({ paramId: 'paramId_0', minValId: 'minValId_0', maxValId: 'maxVal_0' });
  }

  /*get formArray() {
    return this.thirdFormGroup.get('formArray') as FormArray;
  }*/

  createItem(): FormGroup {
    return this._formBuilder.group({
      paramId: '', 
      minValId: '', 
      maxValId: ''
    });
  }

  addItem(): void {
    this.items = this.thirdFormGroup.get('items') as FormArray;
    this.items.push(this.createItem());
  }

  /*addParamsRow() {
    console.log("HERE!!!");
    var num = this.formArray.length - 1;
    var item = {
      paramId: 'paramId_' + num, 
      minValId: 'minValId_' + num, 
      maxValId: 'maxVal_' + num
    };
    this.addItem(item);
  }*/

  removeItem() {
    //this.arrayItems.pop();
    this.items.removeAt(this.items.length - 1);
  }

  /*addItem(item) {
    this.arrayItems.push(item);
    this.formArray.push(this._formBuilder.control(false));
  }*/

  createOnBoardVsBlueprintRequest(blueprints: File[], nsds: File[]/*, translationRules: Object*/) {
    var onBoardVsRequest = JSON.parse('{}');
    onBoardVsRequest['nsds'] = [];
    onBoardVsRequest['translationRules'] = [];
    if (blueprints.length > 0) {
      var blueprint = blueprints[0];
      //console.log(blueprint);

      let promises = [];
      let blueprintPromise = new Promise(resolve => {
          let reader = new FileReader();
          reader.readAsText(blueprint);
          reader.onload = () => resolve(reader.result);
      });
      promises.push(blueprintPromise);

      for (let nsd of nsds) {
          let nsdPromise = new Promise(resolve => {
              let reader = new FileReader();
              reader.readAsText(nsd);
              reader.onload = () => resolve(reader.result);
          });
          promises.push(nsdPromise);
      }

      Promise.all(promises).then(fileContents => {
          onBoardVsRequest['vsBlueprint'] = JSON.parse(fileContents[0]);
          for (var i = 1; i < fileContents.length; i++) {
            onBoardVsRequest['nsds'].push(JSON.parse(fileContents[i]));
          }

          var translationRule = JSON.parse('{}');

          var blueprintId = onBoardVsRequest.vsBlueprint.blueprintId;
          var nsdId = this.thirdFormGroup.get('nsdId').value;
          var nsdVersion = this.thirdFormGroup.get('nsdVersion').value;
          var nsFlavourId = this.thirdFormGroup.get('nsFlavourId').value;
          var nsInstLevel = this.thirdFormGroup.get('nsInstLevel').value;

          translationRule['blueprintId'] = blueprintId;
          translationRule['nsdId'] = nsdId;
          translationRule['nsdVersion'] = nsdVersion;
          translationRule['nsFlavourId'] = nsFlavourId;
          translationRule['nsInstlevel'] = nsInstLevel;

          //var paramId = document.getElementById("paramId_0");
          var paramsRows = this.thirdFormGroup.controls.items as FormArray;
          var controls = paramsRows.controls;
          var paramsObj = [];

          for (var j = 0; j < controls.length; j++) {
            paramsObj.push(controls[j].value);
            console.log(paramsObj);
          }
          translationRule['input'] = paramsObj;
          onBoardVsRequest.translationRules.push(translationRule);

          console.log('onBoardVsRequest: ' + JSON.stringify(onBoardVsRequest, null, 4));
      });
    }

    /*var reader = new FileReader();
     
    reader.onloadend = function (event) {
      console.log(event);
      onBoardVsRequest['vsBlueprint'] = JSON.parse(this.result as string);
      //console.log('onBoardVsRequest: ' + JSON.stringify(onBoardVsRequest, null, 4));

    };
    reader.readAsText(blueprint, "UTF-8");
    console.log(reader.readyState);

    reader = new FileReader();

    for (var i = 0 ; i < nsds.length; i++) {
      var tmpFile = nsds[i];
      reader.onloadend = function (evt) {
        onBoardVsRequest['nsds'].push(JSON.parse(this.result as string));
        console.log('onBoardVsRequest: ' + JSON.stringify(onBoardVsRequest, null, 4));
      };
      reader.readAsText(tmpFile, "UTF-8");
    }*/    
    
      
  }
}
