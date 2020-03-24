import { Component, OnInit, Inject } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
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

  isLinear = true;
  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;
  thirdFormGroup: FormGroup;
  fourthFormGroup: FormGroup;
  fifthFormGroup: FormGroup;

  expBlueprints: ViewValue[] = [];
  expBlueprint = {};
  vsBlueprint: VsBlueprint = new VsBlueprint();
  ctxBlueprints: ViewValue[] = [];
  tcBlueprints: ViewValue[] = [];

  managementTypes: String[] = [
    "PROVIDER_MANAGED",
    "TENANT_MANAGED"
  ];

  ssTypes: String[] = [
    "EMBB",
    "URLLC",
    "M_IOT"
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
      managementType: [''],
      qosParam: ['', Validators.required],
      ssType: [''],
      isPublic: [false]/*,
      priorityType: ['', Validators.required],
      isSharable: [false],
      includeSharable: [false],
      prefProviders: ['', Validators.required],
      notPrefProviders: ['', Validators.required],
      prohibitedProviders: ['', Validators.required],
      timeType: ['', Validators.required],
      coverageType: ['', Validators.required],
      isLowCost: [false]*/
    });
    this.thirdFormGroup = this._formBuilder.group({
    });
    this.fourthFormGroup = this._formBuilder.group({
    });
    this.fifthFormGroup = this._formBuilder.group({
    });
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
    for (var i = 0; i < this.expBlueprints.length; i ++) {
      if (this.expBlueprints[i]['value'] == selectedBlueprint) {
        this.expBlueprint = this.expBlueprints[i]['item'];
        vsbId = this.expBlueprints[i]['item']['vsBlueprintId'];
      }
    }
    var ctxBlueprintIds = this.expBlueprint['ctxBlueprintIds'];
    var tcBlueprintIds = this.expBlueprint['tcBlueprintIds'];
    //console.log(ctxBlueprintIds);
    //console.log(tcBlueprintIds);
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
        //console.log(this.vsBlueprint);
      });
  }

  getCtxBlueprint(ctxBlueprintId: string) {
    this.blueprintsCtxService.getCtxBlueprint(ctxBlueprintId).subscribe((ctxBlueprintInfo: CtxBlueprintInfo) =>
      {
        this.ctxBlueprints.push({value: ctxBlueprintInfo['ctxBlueprintId'], viewValue: ctxBlueprintInfo['ctxBlueprint']['description'], item: ctxBlueprintInfo['ctxBlueprint']});
        //console.log(this.ctxBlueprints);
      });
  }

  getTcBlueprint(tcBlueprintId: string) {
    this.blueprintsTcService.getTcBlueprint(tcBlueprintId).subscribe((tcBlueprintInfo: TcBlueprintInfo) =>
      {
        this.tcBlueprints.push({value: tcBlueprintInfo['testCaseBlueprintId'], viewValue: tcBlueprintInfo['testCaseBlueprint']['description'], item: tcBlueprintInfo['testCaseBlueprint']});
        //console.log(this.tcBlueprints);
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
    onBoardExpRequest['tenantId'] = localStorage.getItem('username');
    onBoardExpRequest['kpiThresholds'] = {};

    if (this.expBlueprint['kpis']) {
      for (var j = 0; j < this.expBlueprint['kpis'].length; j++) {
        onBoardExpRequest['kpiThresholds'][this.expBlueprint['kpis'][j]['kpiId']] =
          this.document.getElementById('metric_' + this.expBlueprint['kpis'][j]['kpiId']).value;
      }
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
    if (this.secondFormGroup.get('managementType').value === '') {
      onBoardExpRequest['vsDescriptor']['managementType'] = "PROVIDER_MANAGED";
    } else {
      onBoardExpRequest['vsDescriptor']['managementType'] = this.secondFormGroup.get('managementType').value;
    }

    var qosParameters = {};

    for (var i = 0; i < this.vsBlueprint['parameters'].length; i++) {
      qosParameters[this.vsBlueprint['parameters'][i]['parameterId']] =
        this.document.getElementById('qos_' + this.vsBlueprint['parameters'][i]['parameterId']).value;
    }

    onBoardExpRequest['vsDescriptor']['qosParameters'] = qosParameters;
    /*onBoardExpRequest['vsDescriptor']['serviceConstraints'] = [];

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
    onBoardExpRequest['vsDescriptor']['isPublic'] = this.secondFormGroup.get('isPublic').value;*/

    for (var i = 0; i < this.tcBlueprints.length; i++) {
      var tempTc = {};
      tempTc['blueprintId'] = this.tcBlueprints[i].value;
      tempTc['parameters'] = {}
      if (this.tcBlueprints[i]['item']['userParameters']) {
        let userParams = new Map(Object.entries(this.tcBlueprints[i]['item']['userParameters']));
        for (let key of userParams.keys()) {
          tempTc['parameters'][key] = this.document.getElementById("user_" + key).value;
        }
      }
      /*
      if (this.tcBlueprints[i]['item']['infrastructureParameters']) {
        let infraParams = new Map(Object.entries(this.tcBlueprints[i]['item']['infrastructureParameters']));
        for (let key of infraParams.keys()) {
          tempTc['parameters'][key] = this.document.getElementById("infra_" + key).value;
        }
      }
      */
      onBoardExpRequest['testCaseConfiguration'].push(tempTc);
    }


    console.log('onBoardExpRequest: ' + JSON.stringify(onBoardExpRequest, null, 4));
    this.descriptorsExpService.postExpDescriptor(onBoardExpRequest)
      .subscribe(expDescriptortId => console.log("Successfully uploaded new Exp Descriptor with id " + expDescriptortId));
  }
}
