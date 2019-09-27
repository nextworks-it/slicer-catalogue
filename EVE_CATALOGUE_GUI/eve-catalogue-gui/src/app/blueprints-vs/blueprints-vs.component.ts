import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { BlueprintsVsDataSource, BlueprintsVsItem } from './blueprints-vs-datasource';

@Component({
  selector: 'app-blueprints-vs',
  templateUrl: './blueprints-vs.component.html',
  styleUrls: ['./blueprints-vs.component.css']
})
export class BlueprintsVsComponent implements AfterViewInit, OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<BlueprintsVsItem>;
  dataSource: BlueprintsVsDataSource;

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'name', 'version', 'description', 'conf_params', 'vsds', 'buttons'];

  ngOnInit() {
    this.dataSource = new BlueprintsVsDataSource();
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.table.dataSource = this.dataSource;
  }
}
