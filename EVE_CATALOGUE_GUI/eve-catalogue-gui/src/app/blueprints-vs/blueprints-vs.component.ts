import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { BlueprintsVsDataSource } from './blueprints-vs-datasource';
import { BlueprintsVsService } from '../blueprints-vs.service';
import { VsBlueprintInfo } from './vs-blueprint-info';
import { VsbGraphService } from  '../vsb-graph.service';

@Component({
  selector: 'app-blueprints-vs',
  templateUrl: './blueprints-vs.component.html',
  styleUrls: ['./blueprints-vs.component.css']
})
export class BlueprintsVsComponent implements OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<VsBlueprintInfo>;
  dataSource: BlueprintsVsDataSource;
  vsBlueprintInfos: VsBlueprintInfo[] = [];

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'name', 'version', 'description', 'conf_params', 'vsds', 'buttons'];

  constructor(private blueprintsVsService: BlueprintsVsService, private vsbGraphService: VsbGraphService, private router: Router) { }

  ngOnInit() {
    this.dataSource = new BlueprintsVsDataSource(this.vsBlueprintInfos);
    this.getVsBlueprints();
  }

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

  deleteVsBlueprint(vsBlueprintId: string) {
    //console.log(vsBlueprintId);
    this.blueprintsVsService.deleteVsBlueprint(vsBlueprintId).subscribe();
  }

  viewVsBlueprintGraph(vsBlueprintId: string) {
    //console.log(vsBlueprintId);
    this.vsbGraphService.updateVSBId(vsBlueprintId);

    //routerLink="/blueprints_vs_graph"
    this.router.navigate(["/blueprints_vs_graph"]);
  }
}
