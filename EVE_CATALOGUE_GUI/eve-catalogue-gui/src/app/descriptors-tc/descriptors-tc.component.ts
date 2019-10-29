import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { DescriptorsTcDataSource } from './descriptors-tc-datasource';
import { TcDescriptorInfo } from './tc-descriptor-info';
import { DescriptorsTcService } from '../descriptors-tc.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-descriptors-tc',
  templateUrl: './descriptors-tc.component.html',
  styleUrls: ['./descriptors-tc.component.css']
})
export class DescriptorsTcComponent implements OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<TcDescriptorInfo>;
  dataSource: DescriptorsTcDataSource;
  tableData: TcDescriptorInfo[] = [];

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'name', 'version', 'testCaseBlueprintId', 'userParameters', 'public'];

  constructor(private descriptorsTcService: DescriptorsTcService, private router: Router) {}

  ngOnInit() {
    this.dataSource = new DescriptorsTcDataSource(this.tableData);
    this.getTestCaseDescriptors();
  }

  getTestCaseDescriptors() {
    this.descriptorsTcService.getTcDescriptors().subscribe((tcDescriptorsInfos: TcDescriptorInfo[]) => 
      {
        //console.log(tcDescriptorsInfos);
        this.tableData = tcDescriptorsInfos;
        this.dataSource = new DescriptorsTcDataSource(this.tableData);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }
}
