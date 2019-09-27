import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { BlueprintsEDetailsDataSource, BlueprintsEDetailsItemKV } from './blueprints-e-details-datasource';

@Component({
  selector: 'app-blueprints-e-details',
  templateUrl: './blueprints-e-details.component.html',
  styleUrls: ['./blueprints-e-details.component.css']
})
export class BlueprintsEDetailsComponent implements AfterViewInit, OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<BlueprintsEDetailsItemKV>;
  dataSource: BlueprintsEDetailsDataSource;

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  /**displayedColumns = ['expBlueprintId', 'name', 'activeExpdId', 'onBoardedNsdInfoId']; */
  displayedColumns = ['key', 'value'];

  ngOnInit() {
    this.dataSource = new BlueprintsEDetailsDataSource();
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.table.dataSource = this.dataSource;
  }
}
