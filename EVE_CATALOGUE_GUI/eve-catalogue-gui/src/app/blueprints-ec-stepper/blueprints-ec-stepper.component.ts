import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { DOCUMENT } from '@angular/common';
import { BlueprintsEcService } from '../blueprints-ec.service';

@Component({
  selector: 'app-blueprints-ec-stepper',
  templateUrl: './blueprints-ec-stepper.component.html',
  styleUrls: ['./blueprints-ec-stepper.component.css']
})
export class BlueprintsEcStepperComponent implements OnInit {

  isLinear = false;
  isButtonVisible = false;
  items: FormArray;
  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;
  thirdFormGroup: FormGroup;

  constructor(@Inject(DOCUMENT) document, private _formBuilder: FormBuilder, private blueprintsEcService: BlueprintsEcService) { }

  ngOnInit() {
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
  }

  createItem(): FormGroup {
    return this._formBuilder.group({
      parameterId: '', 
      minValue: '', 
      maxValue: ''
    });
  }

  addItem(): void {
    this.items = this.thirdFormGroup.get('items') as FormArray;
    this.items.push(this.createItem());
  }

  removeItem() {
    this.items = this.thirdFormGroup.get('items') as FormArray;
    this.items.removeAt(this.items.length - 1);
  }

  createOnBoardCtxBlueprintRequest(blueprints: File[], nsds: File[]) {
    var onBoardCtxRequest = JSON.parse('{}');
    onBoardCtxRequest['nsds'] = [];
    onBoardCtxRequest['translationRules'] = [];
    if (blueprints.length > 0) {
      var blueprint = blueprints[0];

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
        onBoardCtxRequest['ctxBlueprint'] = JSON.parse(fileContents[0]);
          for (var i = 1; i < fileContents.length; i++) {
            onBoardCtxRequest['nsds'].push(JSON.parse(fileContents[i]));
          }

          var translationRule = JSON.parse('{}');

          var blueprintId = onBoardCtxRequest.ctxBlueprint.blueprintId;
          var nsdId = this.thirdFormGroup.get('nsdId').value;
          var nsdVersion = this.thirdFormGroup.get('nsdVersion').value;
          var nsFlavourId = this.thirdFormGroup.get('nsFlavourId').value;
          var nsInstLevel = this.thirdFormGroup.get('nsInstLevel').value;

          //translationRule['blueprintId'] = blueprintId;
          translationRule['nsdId'] = nsdId;
          translationRule['nsdVersion'] = nsdVersion;
          translationRule['nsFlavourId'] = nsFlavourId;
          translationRule['nsInstantiationLevelId'] = nsInstLevel;

          var paramsRows = this.thirdFormGroup.controls.items as FormArray;
          var controls = paramsRows.controls;
          var paramsObj = [];

          for (var j = 0; j < controls.length; j++) {
            paramsObj.push(controls[j].value);
            //console.log(paramsObj);
          }
          translationRule['input'] = paramsObj;
          onBoardCtxRequest.translationRules.push(translationRule);

          console.log('onBoardCtxRequest: ' + JSON.stringify(onBoardCtxRequest, null, 4));

          this.blueprintsEcService.postCtxBlueprint(onBoardCtxRequest)
          .subscribe(ctxBlueprintId => console.log("Ctx Blueprint with id " + ctxBlueprintId));
      });
    }      
  }

}
