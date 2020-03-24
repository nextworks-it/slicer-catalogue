import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { DOCUMENT } from '@angular/common';
import { BlueprintsVsService } from '../blueprints-vs.service';

@Component({
  selector: 'app-blueprints-vs-stepper',
  templateUrl: './blueprints-vs-stepper.component.html',
  styleUrls: ['./blueprints-vs-stepper.component.css']
})
export class BlueprintsVsStepperComponent implements OnInit {

  nsdObj: Object;

  vsbObj: Object;

  dfs: String[] = [];

  instLevels: String[] = [];

  translationParams: String[] = [];

  isLinear = true;
  items: FormArray;
  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;
  thirdFormGroup: FormGroup;

  constructor(@Inject(DOCUMENT) document, private _formBuilder: FormBuilder, private blueprintsVsService: BlueprintsVsService) {
  }

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

  onUploadedVsb(event: any, vsbs: File[]) {
    //console.log(event);

    let promises = [];

    for (let vsb of vsbs) {
        let vsbPromise = new Promise(resolve => {
            let reader = new FileReader();
            reader.readAsText(vsb);
            reader.onload = () => resolve(reader.result);
        });
        promises.push(vsbPromise);
    }

    Promise.all(promises).then(fileContents => {
        this.vsbObj = JSON.parse(fileContents[0]);

        console.log(JSON.stringify(this.vsbObj, null, 4));

        this.translationParams = this.vsbObj['parameters'];
    });
  }

  onUploadedNsd(event: any, nsds: File[]) {
    //console.log(event);
    //this.uploadedNsdName = event.target.files[0].name;

    let promises = [];

    for (let nsd of nsds) {
        let nsdPromise = new Promise(resolve => {
            let reader = new FileReader();
            reader.readAsText(nsd);
            reader.onload = () => resolve(reader.result);
        });
        promises.push(nsdPromise);
    }

    Promise.all(promises).then(fileContents => {
        this.nsdObj = JSON.parse(fileContents[0]);

        console.log(JSON.stringify(this.nsdObj, null, 4));

        this.thirdFormGroup.get('nsdId').setValue(this.nsdObj['nsdIdentifier']);
        this.thirdFormGroup.get('nsdVersion').setValue(this.nsdObj['version']);

        this.dfs = this.nsdObj['nsDf'];

        //this.fourthFormGroup.get('nsFlavourIdCtrl').setValue(nsdObj['nsDf'][0]['nsDfId']);
        //this.fourthFormGroup.get('nsInstLevelIdCtrl').setValue(nsdObj['nsDf'][0]['nsInstantiationLevel'][0]['nsLevelId']);
    });
  }

  onNsDfSelected(event:any) {
    var selectedDf = event.value;

    for (var i = 0; i < this.nsdObj['nsDf'].length; i++) {
      if (this.nsdObj['nsDf'][i]['nsDfId'] == selectedDf) {
        this.instLevels = this.nsdObj['nsDf'][i]['nsInstantiationLevel'];
      }
    }
    alert(this.instLevels);
  }

  createOnBoardVsBlueprintRequest(blueprints: File[], nsds: File[]) {
    if(!this.thirdFormGroup.invalid){
    var onBoardVsRequest = JSON.parse('{}');
    onBoardVsRequest['nsds'] = [];
    onBoardVsRequest['translationRules'] = [];
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
          onBoardVsRequest.translationRules.push(translationRule);

          console.log('onBoardVsRequest: ' + JSON.stringify(onBoardVsRequest, null, 4));

          this.blueprintsVsService.postVsBlueprint(onBoardVsRequest)
          .subscribe(vsBlueprintId => console.log("VS Blueprint with id " + vsBlueprintId));
      });
    }
  }
  }
}
