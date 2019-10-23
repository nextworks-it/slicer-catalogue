import { Component, OnInit } from '@angular/core';
import { FormArray, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { DescriptorsExpService } from '../descriptors-exp.service';
import { BlueprintsVsService } from '../blueprints-vs.service';
import { BlueprintsExpService } from '../blueprints-exp.service';
import { VsBlueprintInfo } from '../blueprints-vs/vs-blueprint-info';
import { ExpBlueprintInfo } from '../blueprints-e/exp-blueprint-info';
import { VsBlueprint } from '../blueprints-vs/vs-blueprint';

export interface ViewValue {
  value: String;
  viewValue: String;
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
  vsBlueprintParameters: Object[] = [];

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
    "No Requirements",
    "LOW",
    "MEDIUM"
  ];

  coverageTypes: String[] = [
    "No Requirements",
    "MEDIUM",
    "HIGH"
  ];

  panelOpenState = false;

  constructor(private _formBuilder: FormBuilder,
    private descriptorsExpService: DescriptorsExpService,
    private blueprintsVsService: BlueprintsVsService,
    private blueprintsExpService: BlueprintsExpService) { }

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
      isPublic: ['', Validators.required],
      priorityType: ['', Validators.required],
      isSharable: ['', Validators.required],
      includeSharable: ['', Validators.required],
      prefProviders: ['', Validators.required],
      notPrefProviders: ['', Validators.required],
      prohibitedProviders: ['', Validators.required],
      timeType: ['', Validators.required],
      coverageType: ['', Validators.required],
      isLowCost: ['', Validators.required],
      params: this._formBuilder.array([])
    });
    this.thirdFormGroup = this._formBuilder.group({
      kpis: this._formBuilder.array([this.createKPI()])
    });
    this.fourthFormGroup = this._formBuilder.group({
      vsDescName: ['', Validators.required]
    });
    this.fifthFormGroup = this._formBuilder.group({
      vsDescName: ['', Validators.required]
    });
  }

  createParam(value: string) {
    return this._formBuilder.group({
      parameterId: value
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
        vsbId = this.expBlueprints[i]['item']['vsBlueprintId'];
      }
    }
   
    this.getVsBlueprint(vsbId);
  }

  getVsBlueprint(vsBlueprintId: string) {
    this.blueprintsVsService.getVsBlueprint(vsBlueprintId).subscribe((vsBlueprintInfo: VsBlueprintInfo) => 
      {
        this.vsBlueprint = vsBlueprintInfo['vsBlueprint'];
        this.vsBlueprintParameters = this.vsBlueprint['parameters'];
        for (var i = 0; i < this.vsBlueprintParameters.length; i++) {
          this.addParam(this.vsBlueprintParameters[i]['parameterName']);
        }
        console.log(this.vsBlueprint);
      });
  }



}
