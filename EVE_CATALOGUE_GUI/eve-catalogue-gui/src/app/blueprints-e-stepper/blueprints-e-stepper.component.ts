import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, FormControl, Validators } from '@angular/forms';
import { NumberSymbol } from '@angular/common';

export interface Site {
  value: string;
  viewValue: string;
}

export interface VSB {
  value: string;
  viewValue: string;
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
  selectedCbs: string[];
  uploadedNsdName: string;
  uploadedMetricsName: string;
  uploadedKPIsName: string;
  uploadedScriptNames: string[];

  sites: Site[] = [
    {value: 'turin-0', viewValue: 'Turin'},
    {value: 'athens-1', viewValue: 'Athens'},
    {value: 'madrid-2', viewValue: 'Madrid'},
    {value: 'paris-3', viewValue: 'Paris'},
    {value: 'rennes-4', viewValue: 'Rennes'},
    {value: 'nice-5', viewValue: 'Nice'}
  ];

  turin_vsbs: VSB[] = [
    {value: 'Automotive Video Streaming Service', viewValue: 'VideoStreamingTurin'}
  ]

  athens_vsbs: VSB[] = [
    {value: 'Automotive Video Streaming Service', viewValue: 'VideoStreamingAthens'}
  ]

  madrid_vsbs: VSB[] = [
    {value: 'Automotive Video Streaming Service', viewValue: 'VideoStreamingMadrid'}
  ]

  paris_vsbs: VSB[] = [
    {value: 'Automotive Video Streaming Service', viewValue: 'VideoStreamingParis'}
  ]

  nice_vsbs: VSB[] = [
    {value: 'Automotive Video Streaming Service', viewValue: 'VideoStreamingNice'}
  ]

  rennes_vsbs: VSB[] = [
    {value: 'Automotive Video Streaming Service', viewValue: 'VideoStreamingRennes'}
  ]

  cbs: VSB[] = [
    {value: "cb_test1", viewValue: "cb_test1"},
    {value: "cb_test2", viewValue: "cb_test2"}
  ]

  vsbs = this.turin_vsbs;

  zeroFormGroup: FormGroup;
  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;
  thirdFormGroup: FormGroup;
  fourthFormGroup: FormGroup;
  fifthFormGroup: FormGroup;
  sixthFormGroup: FormGroup;

  arrayItems: {
    paramId: string;
    minValId: string;
    maxValId: string;
  }[];

  constructor(private _formBuilder: FormBuilder) {
  }

  ngOnInit() {
    this.arrayItems = [
      { paramId: 'paramId_0', minValId: 'minValId_0', maxValId: 'maxVal_0' }
    ];
    this.selectedCbs = [];
    this.uploadedScriptNames = [];
    this.zeroFormGroup = this._formBuilder.group({
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
      formArray: this._formBuilder.array([])
    });
    this.fifthFormGroup = this._formBuilder.group({
      uploadMetricsCtrl: ['', Validators.required],
      uploadKPIsCtrl: ['', Validators.required]
    });
    this.sixthFormGroup = this._formBuilder.group({
      uploadScriptsCtrl: ['', Validators.required]
    });
  }

  onSiteSelected(event) {
    console.log(event);
    if (event.value == 'turin-0') {
      this.vsbs = this.turin_vsbs;
      this.selectedSite = event.value;
    }

    if (event.value == 'athens-1') {
      this.vsbs = this.athens_vsbs;
      this.selectedSite = event.value;
    }

    if (event.value == 'madrid-2') {
      this.vsbs = this.madrid_vsbs;
      this.selectedSite = event.value;
    }

    if (event.value == 'paris-3') {
      this.vsbs = this.paris_vsbs;
      this.selectedSite = event.value;
    }

    if (event.value == 'nice-5') {
      this.vsbs = this.nice_vsbs;
      this.selectedSite = event.value;
    }

    if (event.value == 'rennes-4') {
      this.vsbs = this.rennes_vsbs;
      this.selectedSite = event.value;
    }
  }

  onVsbSelected(event) {
    console.log(event);
    this.selectedVsb = event.value;
  }

  onNameGiven(event) {
    console.log(event);
    this.expBlueprintName = event.target.value;
  }

  onSelectedCb(event) {
    console.log(event);
    this.selectedCbs.push(event.value);
    console.log(this.selectedCbs);
  }

  onUploadedNsd(event) {
    console.log(event);
    this.uploadedNsdName = event.target.files[0].name;
  }

  onUploadedMetrics(event) {
    console.log(event);
    this.uploadedMetricsName = event.target.files[0].name;
  }

  onUploadedKPIs(event) {
    console.log(event);
    this.uploadedKPIsName = event.target.files[0].name;
  }

  onUploadedScripts(event) {
    console.log(event);
    for (var i = 0; i < event.target.files.length; i ++) {
      this.uploadedScriptNames.push(event.target.files[i].name);
    }
  }

  get formArray() {
    return this.fourthFormGroup.get('formArray') as FormArray;
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
