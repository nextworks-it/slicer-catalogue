import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
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
  obj: Object;
}

@Component({
  selector: 'app-blueprints-e-stepper',
  templateUrl: './blueprints-e-stepper.component.html',
  styleUrls: ['./blueprints-e-stepper.component.css']
})

export class BlueprintsEStepperComponent implements OnInit {

  isLinear = true;

  selectedSite: string;
  selectedVsb: string;
  expBlueprintName: string;
  selectedCbs: string[] = [];
  uploadedNsdName: string;
  parameterNames: string[] = [];
  metricNames: string[] = [];
  kpiNames: string[] = [];
  selectedTcbs: string[] = [];

  sites: Site[] = [
    {value: 'ITALY_TURIN', viewValue: 'Turin, Italy'},
    {value: 'GREECE_ATHENS', viewValue: 'Athens, Greece'},
    {value: 'SPAIN_5TONIC', viewValue: 'Madrid, Spain'},
    {value: 'FRANCE_PARIS', viewValue: 'Paris, France'},
    {value: 'FRANCE_RENNES', viewValue: 'Rennes, France'},
    {value: 'FRANCE_NICE', viewValue: 'Nice, France'}
  ];

  metricTypes: String[] = [
    "LOST_PKT",
    "RECEIVED_PKT",
    "SENT_PKT",
    "BANDWIDTH",
    "LATENCY",
    "JITTER",
    "CPU_CONSUMPTION",
    "MEMORY_CONSUMPTION",
    "OTHER"
  ];

  collectionTypes: String[] = [
    "CUMULATIVE",
    "DELTA",
    "GAUGE"
  ];

  nsdObj: Object;

  dfs: String[] = [];

  instLevels: String[] = [];

  translationParams: String[] = [];

  vsbs: Blueprint[] = [];
  ctxbs: Blueprint[] = [];
  tcbs: Blueprint[] = [];

  items: FormArray;
  metric_items: FormArray;
  kpi_items: FormArray;
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
      bpDescriptionCtrl: ['']
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
      nsFlavourIdCtrl: ['', Validators.required],
      nsInstLevelIdCtrl: ['', Validators.required],
      items: this._formBuilder.array([this.createItem()])
    });
    this.fifthFormGroup = this._formBuilder.group({
      metric_items: this._formBuilder.array([this.createMetricItem()]),
      kpi_items: this._formBuilder.array([this.createKPIItem()])
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

  createMetricItem(): FormGroup {
    return this._formBuilder.group({
      iMetricType: '',
      interval: '',
      metricCollectionType: '',
      metricId: '',
      name: '',
      unit: ''
    });
  }

  createKPIItem(): FormGroup {
    return this._formBuilder.group({
      formula: '',
      interval: '',
      metricIds: '',
      kpiId: '',
      name: '',
      unit: ''
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

  addMetricItem(): void {
    this.metric_items = this.fifthFormGroup.get('metric_items') as FormArray;
    this.metric_items.push(this.createMetricItem());
  }

  removeMetricItem() {
    this.metric_items = this.fifthFormGroup.get('metric_items') as FormArray;
    this.metric_items.removeAt(this.metric_items.length - 1);
  }

  addKPIItem(): void {
    this.kpi_items = this.fifthFormGroup.get('kpi_items') as FormArray;
    this.kpi_items.push(this.createKPIItem());
  }

  removeKPIItem() {
    this.kpi_items = this.fifthFormGroup.get('kpi_items') as FormArray;
    this.kpi_items.removeAt(this.kpi_items.length - 1);
  }

  onSiteSelected(event: any) {
    //console.log(event);
    this.selectedSite = event.value;
  }

  onVsbSelected(event: any) {
    //console.log(event);
    this.selectedVsb = event.value;

    for (var i = 0; i < this.vsbs.length; i ++) {
      if (this.vsbs[i]['obj']['blueprintId'] == event.value) {
        for (var j = 0; j < this.vsbs[i]['obj']['parameters'].length; j++) {
          this.translationParams.push(this.vsbs[i]['obj']['parameters'][j]);
        }
        console.log(this.translationParams);
      }
    }
  }

  onNameGiven(event: any) {
    //console.log(event);
    this.expBlueprintName = event.target.value;
  }

  onSelectedCb(event: any) {
    //console.log(event);
    this.selectedCbs.push(event.value);

    for (var i = 0; i < this.ctxbs.length; i ++) {
      if (this.ctxbs[i]['obj']['blueprintId'] == event.value) {
        for (var j = 0; j < this.ctxbs[i]['obj']['parameters'].length; j++) {
          this.translationParams.push(this.ctxbs[i]['obj']['parameters'][j]);
        }
        console.log(this.translationParams);
      }
    }
  }

  onUploadedNsd(event: any, nsds: File[]) {
    //console.log(event);
    this.uploadedNsdName = event.target.files[0].name;

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

        this.fourthFormGroup.get('nsdIdCtrl').setValue(this.nsdObj['nsdIdentifier']);
        this.fourthFormGroup.get('nsdVersionCtrl').setValue(this.nsdObj['version']);

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
  }

  onNsInstLevelSelected(event:any) {
    var selectedInstLevel = event.value;

    for (var i = 0; i < this.instLevels.length; i++) {
      if (this.instLevels[i]['nsLevelId'] == selectedInstLevel) {

      }
    }
  }

  onParameterGiven(event: any) {
    //console.log(event);
    this.parameterNames.push(event.value);
  }

  onMetricGiven(event: any) {
    //console.log(event);
    this.metricNames.push(event.target.value);
  }

  onKPIGiven(event: any) {
    //console.log(event);
    this.kpiNames.push(event.target.value);
  }

  onSelectedTcbs(event: any) {
    //console.log(event);
    this.selectedTcbs.push(event.value);
  }

  getVsBlueprints() {
    this.blueprintsVsService.getVsBlueprints().subscribe((vsBlueprintInfos: VsBlueprintInfo[]) =>
      {
        for (var i = 0; i < vsBlueprintInfos.length; i++) {
          this.vsbs.push({value: vsBlueprintInfos[i]['vsBlueprintId'], viewValue: vsBlueprintInfos[i]['vsBlueprint']['description'], sites: vsBlueprintInfos[i]['vsBlueprint']['compatibleSites'], obj: vsBlueprintInfos[i]['vsBlueprint']});
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
          this.ctxbs.push({value: ctxBlueprintInfos[i]['ctxBlueprintId'], viewValue: ctxBlueprintInfos[i]['ctxBlueprint']['description'], sites: ctxBlueprintInfos[i]['ctxBlueprint']['compatibleSites'], obj: ctxBlueprintInfos[i]['ctxBlueprint']});
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
          this.tcbs.push({value: tcBlueprintInfos[i]['testCaseBlueprintId'], viewValue: tcBlueprintInfos[i]['testCaseBlueprint']['description'], sites: tcBlueprintInfos[i]['testCaseBlueprint']['compatibleSites'], obj: tcBlueprintInfos[i]['testCaseBlueprint']});
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
        for (var i = 0; i < fileContents.length; i++) {
          onBoardExpRequest['nsds'].push(JSON.parse(fileContents[i]));
        }

        var translationRule = JSON.parse('{}');

        var blueprintId = this.zeroFormGroup.get('bpIdCtrl').value;
        var blueprintName = this.zeroFormGroup.get('bpNameCtrl').value;
        var bluepritnVersion = this.zeroFormGroup.get('bpVersionCtrl').value;
        var blueprintDesc = this.zeroFormGroup.get('bpDescriptionCtrl').value;
        var nsdId = this.fourthFormGroup.get('nsdIdCtrl').value;
        var nsdVersion = this.fourthFormGroup.get('nsdVersionCtrl').value;
        var nsFlavourId = this.fourthFormGroup.get('nsFlavourIdCtrl').value;
        var nsInstLevel = this.fourthFormGroup.get('nsInstLevelIdCtrl').value;

        //expBlueprint['expBlueprintId'] = blueprintId;
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

        var paramsRows = this.fourthFormGroup.controls.items as FormArray;
        var controls = paramsRows.controls;
        var paramsObj = [];

        for (var j = 0; j < controls.length; j++) {
          paramsObj.push(controls[j].value);
          //console.log(paramsObj);
        }
        translationRule['input'] = paramsObj;
        onBoardExpRequest.translationRules.push(translationRule);

        /*var metrics = this.fifthFormGroup.controls.metric_items as FormArray;
        var metric_controls = metrics.controls;
        var metricsObj = [];

        for (var j = 0; j < metric_controls.length; j++) {
          metricsObj.push(metric_controls[j].value);
          //console.log(metricsObj);
        }

        var kpis = this.fifthFormGroup.controls.kpi_items as FormArray;
        var kpi_controls = kpis.controls;
        var kpisObj = [];

        for (var j = 0; j < kpi_controls.length; j++) {
          var temp = kpi_controls[j].value;
          var mIds = temp['metricIds'].split(',');

          var newKpiObj = JSON.parse('{}');
          newKpiObj['formula'] = temp['formula'];
          newKpiObj['interval'] = temp['interval'];
          newKpiObj['name'] = temp['name'];
          newKpiObj['kpiId'] = temp['kpiId'];
          newKpiObj['unit'] = temp['unit'];
          newKpiObj['metricIds'] = [];
          for (var h = 0; h < mIds.length; h++) {
            newKpiObj['metricIds'].push(mIds[h].trim());
          }
          kpisObj.push(newKpiObj);
          //console.log(kpisObj);
        }

        expBlueprint['metrics'] = metricsObj;
        expBlueprint['kpis'] = kpisObj;*/

        onBoardExpRequest['expBlueprint'] = expBlueprint;

        console.log('onBoardVsRequest: ' + JSON.stringify(onBoardExpRequest, null, 4));

        this.blueprintsExpService.postExpBlueprint(onBoardExpRequest)
        .subscribe(expBlueprintId => console.log("EXP Blueprint with id " + expBlueprintId));
    });
  }
}
