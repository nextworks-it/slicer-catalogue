import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { BlueprintsEcDataSource } from './blueprints-ec-datasource';
import { BlueprintsEcService } from '../blueprints-ec.service';
import { Router } from '@angular/router';
import { CtxBlueprintInfo } from './ctx-blueprint-info';
import { EcbDetailsService } from '../ecb-details.service';

@Component({
  selector: 'app-blueprints-ec',
  templateUrl: './blueprints-ec.component.html',
  styleUrls: ['./blueprints-ec.component.css']
})
export class BlueprintsEcComponent implements OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<CtxBlueprintInfo>;
  dataSource: BlueprintsEcDataSource;
  ctxBlueprintInfos: CtxBlueprintInfo[] = [];

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'name', 'version', 'description', 'conf_params', 'ctxds', 'buttons'];

  constructor(private blueprintsEcService: BlueprintsEcService, private ecbDetailsService: EcbDetailsService, private router: Router) { }

  ngOnInit() {
    this.dataSource = new BlueprintsEcDataSource(this.ctxBlueprintInfos);
    this.getEcBlueprints();
  }

  getEcBlueprints(): void {
    this.blueprintsEcService.getCtxBlueprints().subscribe((ctxBlueprintInfos: CtxBlueprintInfo[]) => 
      {
        //console.log(ctxBlueprintInfos);
        this.ctxBlueprintInfos = ctxBlueprintInfos;
        this.dataSource = new BlueprintsEcDataSource(this.ctxBlueprintInfos);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }

  deleteEcBlueprint(ctxBlueprintId: string) {
    //console.log(ctxBlueprintId);
    this.blueprintsEcService.deleteCtxBlueprint(ctxBlueprintId).subscribe();
  }

  viewEcBlueprintGraph(ctxBlueprintId: string) {
    //console.log(ctxBlueprintId);
    this.ecbDetailsService.updateCTXBId(ctxBlueprintId);

    this.router.navigate(["/blueprints_ec_details"]);
  }
}
