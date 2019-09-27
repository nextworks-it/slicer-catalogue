import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { DescriptorsVsDataSource, DescriptorsVsItem } from './descriptors-vs-datasource';

@Component({
  selector: 'app-descriptors-vs',
  templateUrl: './descriptors-vs.component.html',
  styleUrls: ['./descriptors-vs.component.css']
})
export class DescriptorsVsComponent implements AfterViewInit, OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<DescriptorsVsItem>;
  dataSource: DescriptorsVsDataSource;

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'name', 'service', 'parameters', 'buttons'];

  ngOnInit() {
    this.dataSource = new DescriptorsVsDataSource();
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.table.dataSource = this.dataSource;
  }
}
