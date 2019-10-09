import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { BlueprintsVsDataSource } from './blueprints-vs-datasource';
import { BlueprintsVsService } from '../blueprints-vs.service';
import { VsBlueprintInfo } from './vs-blueprint-info';

@Component({
  selector: 'app-blueprints-vs',
  templateUrl: './blueprints-vs.component.html',
  styleUrls: ['./blueprints-vs.component.css']
})
export class BlueprintsVsComponent implements /*AfterViewInit,*/ OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<VsBlueprintInfo>;
  dataSource: BlueprintsVsDataSource;
  vsBlueprintInfos: VsBlueprintInfo[] = [];

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'name', 'version', 'description', 'conf_params', 'vsds', 'buttons'];

  constructor(private blueprintsVsService: BlueprintsVsService) { }

  ngOnInit() {
    this.dataSource = new BlueprintsVsDataSource(this.vsBlueprintInfos);
    this.getVsBlueprints();
  }

  /*ngAfterViewInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.table.dataSource = this.dataSource;
  }*/

  getVsBlueprints(): void {
    this.blueprintsVsService.getVsBlueprints().subscribe((vsBlueprintInfos: VsBlueprintInfo[]) => 
      {
        //console.log(vsBlueprintInfos);
        this.vsBlueprintInfos = vsBlueprintInfos;
        this.dataSource = new BlueprintsVsDataSource(this.vsBlueprintInfos);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }
}
