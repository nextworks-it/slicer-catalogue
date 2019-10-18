import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { TcBlueprintInfo } from './tc-blueprint-info';
import { BlueprintsTcDataSource } from './blueprints-tc-datasource';
import { BlueprintsTcService } from '../blueprints-tc.service';
import { FormBuilder, FormArray, FormGroup, Validators } from '@angular/forms';

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

  user_items: FormArray;
  infra_items: FormArray;
  tcFormGroup: FormGroup;

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'name', 'version', 'description', 'script', 'user_params', 'infra_params', 'tcds', 'buttons'];

  constructor(private _formBuilder: FormBuilder, private blueprintsTcService: BlueprintsTcService) { }

  ngOnInit() {
    this.tcFormGroup = this._formBuilder.group({
      description: ['', Validators.required],
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
        this.dataSource = new BlueprintsTcDataSource(this.tcBlueprintInfos);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }

  deleteTcBlueprint(tcBlueprintId: string) {
    //console.log(tcBlueprintId);
    this.blueprintsTcService.deleteTcBlueprint(tcBlueprintId).subscribe();
  }

  createOnBoardTcBlueprintRequest() {
    
  }
}
