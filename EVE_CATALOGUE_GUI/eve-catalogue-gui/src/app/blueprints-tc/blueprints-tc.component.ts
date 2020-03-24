import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { TcBlueprintInfo } from './tc-blueprint-info';
import { BlueprintsTcDataSource } from './blueprints-tc-datasource';
import { BlueprintsTcService } from '../blueprints-tc.service';
import { FormBuilder, FormArray, FormGroup, Validators } from '@angular/forms';
import { DescriptorsTcService } from '../descriptors-tc.service';

@Component({
  selector: 'app-blueprints-tc',
  templateUrl: './blueprints-tc.component.html',
  styleUrls: ['./blueprints-tc.component.css']
})
export class BlueprintsTcComponent implements OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<TcBlueprintInfo>;
  dataSource: BlueprintsTcDataSource;
  tcBlueprintInfos: TcBlueprintInfo[] = [];
  idToTcdIds: Map<string, Map<string, string>> = new Map();

  user_items: FormArray;
  infra_items: FormArray;
  tcFormGroup: FormGroup;

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'name', 'version', 'description', /*'script',*/ 'user_params', 'infra_params',/* 'tcds',*/ 'buttons'];

  constructor(private _formBuilder: FormBuilder,
    private blueprintsTcService: BlueprintsTcService,
    private descriptorsTcService: DescriptorsTcService,
    private router: Router) { }

  ngOnInit() {
    this.tcFormGroup = this._formBuilder.group({
      description: [''],
      name: ['', Validators.required],
      version: ['', Validators.required],
      script: ['', Validators.required],
      user_items: this._formBuilder.array([this.createUserItem()]),
      infra_items: this._formBuilder.array([this.createInfraItem()])
    });
    this.dataSource = new BlueprintsTcDataSource(this.tcBlueprintInfos);
    this.getTcBlueprints();
  }

  createUserItem(): FormGroup {
    return this._formBuilder.group({
      userParamName: '',
      userParamValue: ''
    });
  }

  createInfraItem(): FormGroup {
    return this._formBuilder.group({
      infraParamName: '',
      infraParamValue: ''
    });
  }

  addUserItem(): void {
    this.user_items = this.tcFormGroup.get('user_items') as FormArray;
    this.user_items.push(this.createUserItem());
  }

  removeUserItem() {
    this.user_items = this.tcFormGroup.get('user_items') as FormArray;
    this.user_items.removeAt(this.user_items.length - 1);
  }

  addInfraItem(): void {
    this.infra_items = this.tcFormGroup.get('infra_items') as FormArray;
    this.infra_items.push(this.createInfraItem());
  }

  removeInfraItem() {
    this.infra_items = this.tcFormGroup.get('infra_items') as FormArray;
    this.infra_items.removeAt(this.infra_items.length - 1);
  }

  getTcBlueprints(): void {
    this.blueprintsTcService.getTcBlueprints().subscribe((tcBlueprintInfos: TcBlueprintInfo[]) =>
      {
        //console.log(tcBlueprintInfos);
        this.tcBlueprintInfos = tcBlueprintInfos;

        for (var i = 0; i < tcBlueprintInfos.length; i++) {
          this.idToTcdIds.set(tcBlueprintInfos[i]['testCaseBlueprintId'], new Map());
          for (var j = 0; j < tcBlueprintInfos[i]['activeTcdId'].length; j++) {
            this.getTcDescriptor(tcBlueprintInfos[i]['testCaseBlueprintId'], tcBlueprintInfos[i]['activeTcdId'][j]);
          }
        }
        this.dataSource = new BlueprintsTcDataSource(this.tcBlueprintInfos);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }

  getTcDescriptor(tcbId: string, tcdId: string) {
    this.descriptorsTcService.getTcDescriptor(tcdId).subscribe(tcDescriptorInfo => {
      var names = this.idToTcdIds.get(tcbId);
      names.set(tcdId, tcDescriptorInfo['name']);
    })
  }

  viewTcDescriptor(tcdId: string) {
    //console.log(tcdId);
    localStorage.setItem('tcdId', tcdId);

    this.router.navigate(["/descriptors_tc"]);
  }

  deleteTcBlueprint(tcBlueprintId: string) {
    //console.log(tcBlueprintId);
    this.blueprintsTcService.deleteTcBlueprint(tcBlueprintId).subscribe();
  }

  createOnBoardTcBlueprintRequest() {
    if(! this.tcFormGroup.invalid){
      var onBoardTcRequest = JSON.parse('{}');
      var testCaseBlueprint = JSON.parse('{}');

      var description = this.tcFormGroup.get('description').value;
      var name = this.tcFormGroup.get('name').value;
      var script = this.tcFormGroup.get('script').value;
      var version = this.tcFormGroup.get('version').value;

      testCaseBlueprint['description'] = description;
      testCaseBlueprint['name'] = name;
      testCaseBlueprint['script'] = script;
      testCaseBlueprint['version'] = version;

      var userParams = this.tcFormGroup.controls.user_items as FormArray;
      var user_controls = userParams.controls;
      var userParamsMap = JSON.parse('{}');

      for (var j = 0; j < user_controls.length; j++) {
        //console.log(user_controls[j].value);
        if ((user_controls[j].value)['userParamName'] != "") {
          userParamsMap[(user_controls[j].value)['userParamName']] = (user_controls[j].value)['userParamValue'];
        }
      }

      testCaseBlueprint['userParameters'] = userParamsMap;

      var infraParams = this.tcFormGroup.controls.infra_items as FormArray;
      var infra_controls = infraParams.controls;
      var infraParamsMap = JSON.parse('{}');

      for (var j = 0; j < infra_controls.length; j++) {
        //console.log(infra_controls[j].value);
        if ((infra_controls[j].value)['infraParamName'] != "") {
          infraParamsMap[(infra_controls[j].value)['infraParamName']] = (infra_controls[j].value)['infraParamValue'];
        }
      }

      testCaseBlueprint['infrastructureParameters'] = infraParamsMap;

      onBoardTcRequest['testCaseBlueprint'] = testCaseBlueprint;

      console.log("OnboardTcRequest:" + JSON.stringify(onBoardTcRequest, null, 4));

      this.blueprintsTcService.postTcBlueprint(onBoardTcRequest)
      .subscribe(tcBlueprintId => console.log("TC Blueprint with id " + tcBlueprintId));
    }

  }
}
