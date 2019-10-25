import { Component, OnInit, Inject } from '@angular/core';
import { FormArray, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { DescriptorsExpService } from '../descriptors-exp.service';
import { BlueprintsVsService } from '../blueprints-vs.service';
import { BlueprintsExpService } from '../blueprints-exp.service';
import { VsBlueprintInfo } from '../blueprints-vs/vs-blueprint-info';
import { ExpBlueprintInfo } from '../blueprints-e/exp-blueprint-info';
import { VsBlueprint } from '../blueprints-vs/vs-blueprint';
import { BlueprintsEcService } from '../blueprints-ec.service';
import { BlueprintsTcService } from '../blueprints-tc.service';
import { CtxBlueprintInfo } from '../blueprints-ec/ctx-blueprint-info';
import { TcBlueprintInfo } from '../blueprints-tc/tc-blueprint-info';
import { DOCUMENT } from '@angular/common';

export interface ViewValue {
  value: string;
  viewValue: string;
  item: Object;
}

@Component({
  selector: 'app-descriptors-e-stepper',
  templateUrl: './descriptors-e-stepper.component.html',
  styleUrls: ['./descriptors-e-stepper.component.css']
})
export class DescriptorsEStepperComponent implements OnInit {

  isLinear = false;
  isButtonVisible = false;
  params: FormArray;
  kpis: FormArray;
  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;
  thirdFormGroup: FormGroup;
  fourthFormGroup: FormGroup;
  fifthFormGroup: FormGroup;

  expBlueprints: ViewValue[] = [];
  vsBlueprint: VsBlueprint;
  ctxBlueprints: ViewValue[] = [];
  tcBlueprints: ViewValue[] = [];

  tcbIdsToArraysMap: Map<string, string> = new Map();
  usedArrays: number = 0;

  array_1: FormArray;
  array_2: FormArray;
  array_3: FormArray;
  array_4: FormArray;
  array_5: FormArray;
  array_6: FormArray;
  array_7: FormArray;
  array_8: FormArray;
  array_9: FormArray;
  array_10: FormArray;
  array_11: FormArray;
  array_12: FormArray;
  array_13: FormArray;
  array_14: FormArray;
  array_15: FormArray;
  array_16: FormArray;
  array_17: FormArray;
  array_18: FormArray;
  array_19: FormArray;
  array_20: FormArray;

  managementTypes: String[] = [
    "PROVIDER_MANAGED",
    "TENANT_MANAGED"
  ];

  ssTypes: String[] = [
    "NONE",
    "EMBB",
    "URLLC",
    "M_IOT",
    "ENTERPRISE",
    "NFV_IAAS"
  ];

  priorityTypes: String[] = [
    "LOW",
    "MEDIUM",
    "HIGH"
  ]

  timeTypes: String[] = [
    "SERVICE_CREATION_TIME_LOW",
    "SERVICE_CREATION_TIME_MEDIUM",
    "UNDEFINED"
  ];

  coverageTypes: String[] = [
    "AVAILABILITY_COVERAGE_HIGH",
    "AVAILABILITY_COVERAGE_MEDIUM",
    "UNDEFINED"
  ];

  panelOpenState = false;

  constructor(@Inject(DOCUMENT) private document,
    private _formBuilder: FormBuilder,
    private descriptorsExpService: DescriptorsExpService,
    private blueprintsVsService: BlueprintsVsService,
    private blueprintsExpService: BlueprintsExpService,
    private blueprintsCtxService: BlueprintsEcService,
    private blueprintsTcService: BlueprintsTcService) { }

  ngOnInit() {
    this.getExpBlueprints();
    this.firstFormGroup = this._formBuilder.group({
      expBlueprintId: ['', Validators.required],
      expDescName: ['', Validators.required],
      expDescVersion: ['', Validators.required]
    });
    this.secondFormGroup = this._formBuilder.group({
      vsDescName: ['', Validators.required],
      vsDescVersion: ['', Validators.required],
      managementType: ['', Validators.required],
      ssType: ['', Validators.required],
      isPublic: [false],
      priorityType: ['', Validators.required],
      isSharable: [false],
      includeSharable: [false],
      prefProviders: ['', Validators.required],
      notPrefProviders: ['', Validators.required],
      prohibitedProviders: ['', Validators.required],
      timeType: ['', Validators.required],
      coverageType: ['', Validators.required],
      isLowCost: [false],
      params: this._formBuilder.array([])
    });
    this.thirdFormGroup = this._formBuilder.group({
      kpis: this._formBuilder.array([this.createKPI()])
    });
    this.fourthFormGroup = this._formBuilder.group({
    });
    this.fifthFormGroup = this._formBuilder.group({
      array_1: this._formBuilder.array([this.createProperty()]),
      array_2: this._formBuilder.array([this.createProperty()]),
      array_3: this._formBuilder.array([this.createProperty()]),
      array_4: this._formBuilder.array([this.createProperty()]),
      array_5: this._formBuilder.array([this.createProperty()]),
      array_6: this._formBuilder.array([this.createProperty()]),
      array_7: this._formBuilder.array([this.createProperty()]),
      array_8: this._formBuilder.array([this.createProperty()]),
      array_9: this._formBuilder.array([this.createProperty()]),
      array_10: this._formBuilder.array([this.createProperty()]),
      array_11: this._formBuilder.array([this.createProperty()]),
      array_12: this._formBuilder.array([this.createProperty()]),
      array_13: this._formBuilder.array([this.createProperty()]),
      array_14: this._formBuilder.array([this.createProperty()]),
      array_15: this._formBuilder.array([this.createProperty()]),
      array_16: this._formBuilder.array([this.createProperty()]),
      array_17: this._formBuilder.array([this.createProperty()]),
      array_18: this._formBuilder.array([this.createProperty()]),
      array_19: this._formBuilder.array([this.createProperty()]),
      array_20: this._formBuilder.array([this.createProperty()])
    });
  }

  createParam(value: string) {
    return this._formBuilder.group({
      parameterName: value,
      parameterId: ''
    });
  }

  addParam(value: string): void {
    this.params = this.secondFormGroup.get('params') as FormArray;
    this.params.push(this.createParam(value));
  }

  removeItem() {
    this.params = this.secondFormGroup.get('params') as FormArray;
    this.params.removeAt(this.params.length - 1);
  }

  createKPI() {
    return this._formBuilder.group({
      kpiId: '',
      threshold: ''
    });
  }

  addKPI(): void {
    this.kpis = this.thirdFormGroup.get('kpis') as FormArray;
    this.kpis.push(this.createKPI());
  }

  removeKPI() {
    this.kpis = this.thirdFormGroup.get('kpis') as FormArray;
    this.kpis.removeAt(this.kpis.length - 1);
  }

  createProperty() {
    return this._formBuilder.group({
      propertyName: '',
      propertyValue: ''
    });
  }

  addProperty(tcbId: string): void {
    var arrayName = this.tcbIdsToArraysMap.get(tcbId);
    if (arrayName == "array_1") {
      this.array_1 = this.fifthFormGroup.get('array_1') as FormArray;
      this.array_1.push(this.createProperty());
    }

    if (arrayName == "array_2") {
      this.array_2 = this.fifthFormGroup.get('array_2') as FormArray;
      this.array_2.push(this.createProperty());
    }
    
  }

  removeProperty(tcbId: string) {
    var arrayName = this.tcbIdsToArraysMap.get(tcbId);
    if (arrayName == "array_1") {
      this.array_1 = this.fifthFormGroup.get('array_1') as FormArray;
      this.array_1.removeAt(this.array_1.length - 1);
    }
    
    if (arrayName == "array_2") {
      this.array_2 = this.fifthFormGroup.get('array_2') as FormArray;
      this.array_2.removeAt(this.array_2.length - 1);
    }
  }

  getPropertyArrayLength(tcbId: string): number {
    var arrayName = this.tcbIdsToArraysMap.get(tcbId);
    var length;
    if (arrayName == "array_1") {
      this.array_1 = this.fifthFormGroup.get('array_1') as FormArray;
      length = this.array_1.length;
    }
    
    if (arrayName == "array_2") {
      this.array_2 = this.fifthFormGroup.get('array_2') as FormArray;
      length = this.array_2.length;
    }
    return length;
  }

  getExpBlueprints() {
    this.blueprintsExpService.getExpBlueprints().subscribe((expBlueprintInfos: ExpBlueprintInfo[]) => 
      {
        for (var i = 0; i < expBlueprintInfos.length; i++) {
          this.expBlueprints.push({value: expBlueprintInfos[i]['expBlueprintId'], viewValue: expBlueprintInfos[i]['expBlueprint']['description'], item: expBlueprintInfos[i]['expBlueprint']});
        }

      });
  }

  onExpBSelected(event: any) {
    var selectedBlueprint = event.value;
    var vsbId;
    var expBlueprint;
    for (var i = 0; i < this.expBlueprints.length; i ++) {
      if (this.expBlueprints[i]['value'] == selectedBlueprint) {
        expBlueprint = this.expBlueprints[i]['item'];
        vsbId = this.expBlueprints[i]['item']['vsBlueprintId'];
      }
    }
    var ctxBlueprintIds = expBlueprint['ctxBlueprintIds'];
    var tcBlueprintIds = expBlueprint['tcBlueprintIds'];
    console.log(ctxBlueprintIds);
    console.log(tcBlueprintIds);
    for (var i = 0; i < ctxBlueprintIds.length; i++) {
      this.getCtxBlueprint(ctxBlueprintIds[i]);
    }

    for (var i = 0; i < tcBlueprintIds.length; i++) {
      this.getTcBlueprint(tcBlueprintIds[i]);
    }
    this.getVsBlueprint(vsbId);
  }

  getVsBlueprint(vsBlueprintId: string) {
    this.blueprintsVsService.getVsBlueprint(vsBlueprintId).subscribe((vsBlueprintInfo: VsBlueprintInfo) => 
      {
        this.vsBlueprint = vsBlueprintInfo['vsBlueprint'];
        
        var vsBlueprintParameters = this.vsBlueprint['parameters'];
        for (var i = 0; i < vsBlueprintParameters.length; i++) {
          this.addParam(vsBlueprintParameters[i]['parameterName']);
        }
        console.log(this.vsBlueprint);
      });
  }

  getCtxBlueprint(ctxBlueprintId: string) {
    this.blueprintsCtxService.getCtxBlueprint(ctxBlueprintId).subscribe((ctxBlueprintInfo: CtxBlueprintInfo) => 
      { 
        this.ctxBlueprints.push({value: ctxBlueprintInfo['ctxBlueprintId'], viewValue: ctxBlueprintInfo['ctxBlueprint']['description'], item: ctxBlueprintInfo['ctxBlueprint']});
        console.log(this.ctxBlueprints);
      });
  }

  getTcBlueprint(tcBlueprintId: string) {
    this.blueprintsTcService.getTcBlueprint(tcBlueprintId).subscribe((tcBlueprintInfo: TcBlueprintInfo) => 
      { 
        this.tcBlueprints.push({value: tcBlueprintInfo['testCaseBlueprintId'], viewValue: tcBlueprintInfo['testCaseBlueprint']['description'], item: tcBlueprintInfo['testCaseBlueprint']});
        console.log(this.tcBlueprints);
        this.tcbIdsToArraysMap.set(tcBlueprintInfo['testCaseBlueprintId'], "array_" + (this.usedArrays + 1).toString());
        this.usedArrays = this.usedArrays + 1;
        
      });
  }

  createOnBoardExpDescriptorRequest() {
    var onBoardExpRequest = JSON.parse('{}');
    onBoardExpRequest['testCaseConfiguration'] = [];
    onBoardExpRequest['contextDetails'] = [];
    onBoardExpRequest['vsDescriptor'] = {};

    onBoardExpRequest['experimentBlueprintId'] = this.firstFormGroup.get('expBlueprintId').value;
    onBoardExpRequest['name'] = this.firstFormGroup.get('expDescName').value;
    onBoardExpRequest['version'] = this.firstFormGroup.get('expDescVersion').value;
    onBoardExpRequest['tenantId'] = "admin";
    onBoardExpRequest['kpiThresholds'] = {};

    var kpiRows = this.thirdFormGroup.controls.kpis as FormArray;
    var controls = kpiRows.controls;

    for (var j = 0; j < controls.length; j++) {
      onBoardExpRequest['kpiThresholds'][(controls[j].value)['kpiId']] = (controls[j].value)['threshold'];
    }

    for (var i = 0; i < this.ctxBlueprints.length; i++) {
      var tempCtx = {};
      tempCtx['blueprintId'] = this.ctxBlueprints[i].value;
      tempCtx['parameters'] = {}
      for (var j = 0; j < this.ctxBlueprints[i]['item']['parameters'].length; j++) {
        tempCtx['parameters'][this.ctxBlueprints[i]['item']['parameters'][j]['parameterId']] = 
        this.document.getElementById(this.ctxBlueprints[i]['item']['parameters'][j]['parameterId']).value;
      }
      onBoardExpRequest['contextDetails'].push(tempCtx);
    }

    onBoardExpRequest['vsDescriptor']['name'] = this.secondFormGroup.get('vsDescName').value;
    onBoardExpRequest['vsDescriptor']['version'] = this.secondFormGroup.get('vsDescVersion').value;
    onBoardExpRequest['vsDescriptor']['vsBlueprintId'] = this.vsBlueprint['blueprintId'];
    onBoardExpRequest['vsDescriptor']['sst'] = this.secondFormGroup.get('ssType').value;
    onBoardExpRequest['vsDescriptor']['managementType'] = this.secondFormGroup.get('managementType').value;

    var qosParameters = {}
    var paramsRows = this.secondFormGroup.controls.params as FormArray;
    var pControls = paramsRows.controls;

    for (var i = 0; i < pControls.length; i++) {
      qosParameters[(pControls[i].value)['parameterName']] = (pControls[i].value)['parameterId'];
    }

    onBoardExpRequest['vsDescriptor']['qosParameters'] = qosParameters;
    onBoardExpRequest['vsDescriptor']['serviceConstraints'] = [];

    for (var i = 0; i < this.vsBlueprint['atomicComponents'].length; i++) {
      var tempConstr = {};
      tempConstr['atomicComponentId'] = this.vsBlueprint['atomicComponents'][i]['componentId'];
      tempConstr['canIncludeSharedElements'] = this.secondFormGroup.get('includeSharable').value;
      tempConstr['nonPreferredProviders'] = [this.secondFormGroup.get('notPrefProviders').value];
      tempConstr['preferredProviders'] = [this.secondFormGroup.get('prefProviders').value];
      tempConstr['prohibitedProviders'] = [this.secondFormGroup.get('prohibitedProviders').value];
      tempConstr['priority'] = this.secondFormGroup.get('priorityType').value;
      tempConstr['sharable'] = this.secondFormGroup.get('isSharable').value;
      onBoardExpRequest['vsDescriptor']['serviceConstraints'].push(tempConstr);
    }

    onBoardExpRequest['vsDescriptor']['sla'] = {};
    onBoardExpRequest['vsDescriptor']['sla']['availabilityCoverage'] = this.secondFormGroup.get('coverageType').value;
    onBoardExpRequest['vsDescriptor']['sla']['serviceCreationTime'] = this.secondFormGroup.get('timeType').value;
    onBoardExpRequest['vsDescriptor']['sla']['lowCostRequired'] = this.secondFormGroup.get('isLowCost').value;
    onBoardExpRequest['vsDescriptor']['isPublic'] = this.secondFormGroup.get('isPublic').value;

    for (var i = 0; i < this.tcBlueprints.length; i++) {
      var tempConf = {
        "blueprintId": this.tcBlueprints[i]['value'],
        "parameters": {}
      };
      var arrayName = this.tcbIdsToArraysMap.get(this.tcBlueprints[i]['value']);
      var propArray = this.fifthFormGroup.get(arrayName) as FormArray;
      var propControls = propArray.controls;

      for (var i = 0; i < propControls.length; i++) {
        //console.log(propControls[i]);
        tempConf.parameters[(propControls[i].value)['propertyName']] = (propControls[i].value)['propertyValue'];
      }
      onBoardExpRequest['testCaseConfiguration'].push(tempConf);
    }


    console.log('onBoardExpRequest: ' + JSON.stringify(onBoardExpRequest, null, 4));
    this.descriptorsExpService.postExpDescriptor(onBoardExpRequest)
      .subscribe(expDescriptortId => console.log("Successfully uploaded new Exp Descriptor with id " + expDescriptortId));
  }
}
