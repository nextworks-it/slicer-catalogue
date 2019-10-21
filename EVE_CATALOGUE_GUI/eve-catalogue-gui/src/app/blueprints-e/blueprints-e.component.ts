import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { BlueprintsEDataSource } from './blueprints-e-datasource';
import { ExpBlueprintInfo } from './exp-blueprint-info';
import { BlueprintsExpService } from '../blueprints-exp.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-blueprints-e',
  templateUrl: './blueprints-e.component.html',
  styleUrls: ['./blueprints-e.component.css']
})
export class BlueprintsEComponent implements /*AfterViewInit,*/ OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<ExpBlueprintInfo>;
  dataSource: BlueprintsEDataSource;
  expBlueprintInfos: ExpBlueprintInfo[] = [];
  

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['expBlueprintId', 'name', 'expBlueprintVersion'/*, 'verticalService'*/, 'sites', 'buttons'];

  constructor(private blueprintsExpService: BlueprintsExpService/*, private expbDetailsService: ExpbDetailsService*/, private router: Router) { }

  ngOnInit() {
    this.dataSource = new BlueprintsEDataSource(this.expBlueprintInfos);
    this.getEBlueprints();
  }

  /*ngAfterViewInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.table.dataSource = this.dataSource;
  }*/

  getEBlueprints(): void {
    this.blueprintsExpService.getExpBlueprints().subscribe((expBlueprintInfos: ExpBlueprintInfo[]) => 
      {
        //console.log(expBlueprintInfos);
        this.expBlueprintInfos = expBlueprintInfos;
        this.dataSource = new BlueprintsEDataSource(this.expBlueprintInfos);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }

  deleteEBlueprint(expBlueprintId: string) {
    //console.log(expBlueprintId);
    this.blueprintsExpService.deleteExpBlueprint(expBlueprintId).subscribe();
  }

  viewEBlueprintGraph(expBlueprintId: string) {
    console.log(expBlueprintId);
    //this.expbDetailsService.updateExpBId(expBlueprintId);

    this.router.navigate(["/blueprints_e_details"]);
  }
}
