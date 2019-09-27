import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { BlueprintsEDataSource, BlueprintsEItem } from './blueprints-e-datasource';

@Component({
  selector: 'app-blueprints-e',
  templateUrl: './blueprints-e.component.html',
  styleUrls: ['./blueprints-e.component.css']
})
export class BlueprintsEComponent implements AfterViewInit, OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<BlueprintsEItem>;
  dataSource: BlueprintsEDataSource;

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['expBlueprintId', 'name', 'expBlueprintVersion', 'verticalService', 'sites', 'buttons'];

  ngOnInit() {
    this.dataSource = new BlueprintsEDataSource();
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.table.dataSource = this.dataSource;
  }
}
