import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, FormControl, Validators } from '@angular/forms';
import { NumberSymbol } from '@angular/common';
import { VsBlueprintInfo } from '../blueprints-vs/vs-blueprint-info';
import { BlueprintsVsService } from '../blueprints-vs.service';
import { BlueprintsEcService } from '../blueprints-ec.service';
import { CtxBlueprintInfo } from '../blueprints-ec/ctx-blueprint-info';
import { BlueprintsTcService } from '../blueprints-tc.service';
import { TcBlueprintInfo } from '../blueprints-tc/tc-blueprint-info';
import { BlueprintsExpService } from '../blueprints-exp.service';

export interface Site {
  value: string;
  viewValue: string;
}

export interface Blueprint {
  value: String;
  viewValue: String;
  sites: String[];
}

@Component({
  selector: 'app-blueprints-e-stepper',
  templateUrl: './blueprints-e-stepper.component.html',
  styleUrls: ['./blueprints-e-stepper.component.css']
})

export class BlueprintsEStepperComponent implements OnInit {

  isLinear = false;

  selectedSite: string;
  selectedVsb: string;
  expBlueprintName: string;
  selectedCbs: string[] = [];
  uploadedNsdName: string;
  uploadedMetricsName: string;
  uploadedKPIsName: string;
  selectedTcbs: string[] = [];

  sites: Site[] = [
    {value: 'ITALY_TURIN', viewValue: 'Turin, Italy'},
    {value: 'GREECE_ATHENS', viewValue: 'Athens, Greece'},
    {value: 'SPAIN_5TONIC', viewValue: 'Madrid, Spain'},
    {value: 'FRANCE_PARIS', viewValue: 'Paris, France'},
    {value: 'FRANCE_RENNES', viewValue: 'Rennes, France'},
    {value: 'FRANCE_NICE', viewValue: 'Nice, France'}
  ]; 

  vsbs: Blueprint[] = [];
  ctxbs: Blueprint[] = [];
  tcbs: Blueprint[] = [];

  items: FormArray;
  zeroFormGroup: FormGroup;
  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;
  thirdFormGroup: FormGroup;
  fourthFormGroup: FormGroup;
  fifthFormGroup: FormGroup;
  sixthFormGroup: FormGroup;

  constructor(private _formBuilder: FormBuilder,
    private blueprintsVsService: BlueprintsVsService,
    private blueprintsCtxService: BlueprintsEcService,
    private blueprintsTcService: BlueprintsTcService,
    private blueprintsExpService: BlueprintsExpService
    ) {
  }

  ngOnInit() {
    this.getVsBlueprints();
    this.getCtxBlueprints();
    this.getTcBlueprints();

    this.zeroFormGroup = this._formBuilder.group({
      bpIdCtrl: ['', Validators.required],
      bpNameCtrl: ['', Validators.required],
      bpVersionCtrl: ['', Validators.required],
      bpDescriptionCtrl: ['', Validators.required]
    });
    this.firstFormGroup = this._formBuilder.group({
      selectSiteCtrl: ['', Validators.required],
      selectVsbCtrl: ['', Validators.required]
    });
    this.secondFormGroup = this._formBuilder.group({
      selectCbsCtrl: ['', Validators.required]
    });
    this.thirdFormGroup = this._formBuilder.group({
      uploadNsdCtrl: ['', Validators.required]
    });
    this.fourthFormGroup = this._formBuilder.group({
      nsdIdCtrl: ['', Validators.required],
      nsdVersionCtrl: ['', Validators.required],
      nsdFlavourIdCtrl: ['', Validators.required],
      nsdInstLevelIdCtrl: ['', Validators.required],
      items: this._formBuilder.array([this.createItem()])
    });
    this.fifthFormGroup = this._formBuilder.group({
      uploadMetricsCtrl: ['', Validators.required],
      uploadKPIsCtrl: ['', Validators.required]
    });
    this.sixthFormGroup = this._formBuilder.group({
      selectTcbsCtrl: ['', Validators.required]
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
    this.items = this.fourthFormGroup.get('items') as FormArray;
    this.items.push(this.createItem());
  }

  removeItem() {
    this.items = this.fourthFormGroup.get('items') as FormArray;
    this.items.removeAt(this.items.length - 1);
  }

  onSiteSelected(event: any) {
    console.log(event);
    this.selectedSite = event.value;
  }

  onVsbSelected(event: any) {
    console.log(event);
    this.selectedVsb = event.value;
  }

  onNameGiven(event: any) {
    console.log(event);
    this.expBlueprintName = event.target.value;
  }

  onSelectedCb(event: any) {
    console.log(event);
    this.selectedCbs.push(event.value);
    console.log(this.selectedCbs);
  }

  onUploadedNsd(event: any) {
    console.log(event);
    this.uploadedNsdName = event.target.files[0].name;
  }

  onUploadedMetrics(event: any) {
    console.log(event);
    this.uploadedMetricsName = event.target.files[0].name;
  }

  onUploadedKPIs(event: any) {
    console.log(event);
    this.uploadedKPIsName = event.target.files[0].name;
  }

  onSelectedTcbs(event: any) {
    console.log(event);
    this.selectedTcbs.push(event.value);
  }

  getVsBlueprints() {
    this.blueprintsVsService.getVsBlueprints().subscribe((vsBlueprintInfos: VsBlueprintInfo[]) => 
      {
        for (var i = 0; i < vsBlueprintInfos.length; i++) {
          this.vsbs.push({value: vsBlueprintInfos[i]['vsBlueprintId'], viewValue: vsBlueprintInfos[i]['vsBlueprint']['description'], sites: vsBlueprintInfos[i]['vsBlueprint']['compatibleSites']});
        }
      });
  }

  filterVsbsInSite(){
    return this.vsbs.filter(x => x.sites.indexOf(this.selectedSite) >= 0);
  }

  getCtxBlueprints() {
    this.blueprintsCtxService.getCtxBlueprints().subscribe((ctxBlueprintInfos: CtxBlueprintInfo[]) =>
      {
        for (var i = 0; i < ctxBlueprintInfos.length; i++) {
          this.ctxbs.push({value: ctxBlueprintInfos[i]['ctxBlueprintId'], viewValue: ctxBlueprintInfos[i]['ctxBlueprint']['description'], sites: ctxBlueprintInfos[i]['ctxBlueprint']['compatibleSites']});
        }
      });
  }

  filterCtxbsInSite(){
    return this.ctxbs.filter(x => x.sites.indexOf(this.selectedSite) >= 0);
  }

  getTcBlueprints() {
    this.blueprintsTcService.getTcBlueprints().subscribe((tcBlueprintInfos: TcBlueprintInfo[]) =>
      {
        for (var i = 0; i < tcBlueprintInfos.length; i++) {
          this.tcbs.push({value: tcBlueprintInfos[i]['testCaseBlueprintId'], viewValue: tcBlueprintInfos[i]['testCaseBlueprint']['description'], sites: tcBlueprintInfos[i]['testCaseBlueprint']['compatibleSites']});
        }
      });
  }

  filterTcbsInSite(){
    return this.tcbs.filter(x => x.sites.indexOf(this.selectedSite) >= 0);
  }

  createOnBoardExpBlueprintRequest(nsds: File[]) {
    var onBoardExpRequest = JSON.parse('{}');
    onBoardExpRequest['nsds'] = [];
    onBoardExpRequest['translationRules'] = []

    var expBlueprint = JSON.parse('{}');

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
        for (var i = 1; i < fileContents.length; i++) {
          onBoardExpRequest['nsds'].push(JSON.parse(fileContents[i]));
        }

        var translationRule = JSON.parse('{}');

        var blueprintId = this.zeroFormGroup.get('bpIdCtrl').value;
        var blueprintName = this.zeroFormGroup.get('bpNameCtrl').value;
        var bluepritnVersion = this.zeroFormGroup.get('bpVersionCtrl').value;
        var blueprintDesc = this.zeroFormGroup.get('bpDescriptionCtrl').value;
        var nsdId = this.thirdFormGroup.get('nsdIdCtrl').value;
        var nsdVersion = this.thirdFormGroup.get('nsdVersionCtrl').value;
        var nsFlavourId = this.thirdFormGroup.get('nsFlavourIdCtrl').value;
        var nsInstLevel = this.thirdFormGroup.get('nsInstLevelCtrl').value;


        expBlueprint['expBlueprintId'] = blueprintId;
        expBlueprint['description'] = blueprintDesc;
        expBlueprint['name'] = blueprintName;
        expBlueprint['version'] = bluepritnVersion;

        expBlueprint['vsBlueprintId'] = this.selectedVsb;
        expBlueprint['ctxBlueprintIds'] = this.selectedCbs;
        expBlueprint['tcBlueprintIds'] = this.selectedTcbs;
        expBlueprint['sites'] = [this.selectedSite];

        translationRule['blueprintId'] = blueprintId;
        translationRule['nsdId'] = nsdId;
        translationRule['nsdVersion'] = nsdVersion;
        translationRule['nsFlavourId'] = nsFlavourId;
        translationRule['nsInstantiationLevelId'] = nsInstLevel;

        var paramsRows = this.thirdFormGroup.controls.items as FormArray;
        var controls = paramsRows.controls;
        var paramsObj = [];

        for (var j = 0; j < controls.length; j++) {
          paramsObj.push(controls[j].value);
          console.log(paramsObj);
        }
        translationRule['input'] = paramsObj;
        onBoardExpRequest.translationRules.push(translationRule);

        onBoardExpRequest['expBlueprint'] = expBlueprint;

        console.log('onBoardVsRequest: ' + JSON.stringify(onBoardExpRequest, null, 4));

        this.blueprintsExpService.postExpBlueprint(onBoardExpRequest)
        .subscribe(expBlueprintId => console.log("Successfully uploaded new EXP Blueprint with id " + expBlueprintId));
    });   
  }
}
