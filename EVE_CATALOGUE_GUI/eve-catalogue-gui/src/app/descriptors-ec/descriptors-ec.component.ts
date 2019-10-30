import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { DescriptorsEcDataSource } from './descriptors-ec-datasource';
import { EcDescriptorInfo } from './ec-descriptor-info';
import { DescriptorsEcService } from '../descriptors-ec.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-descriptors-ec',
  templateUrl: './descriptors-ec.component.html',
  styleUrls: ['./descriptors-ec.component.css']
})
export class DescriptorsEcComponent implements OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<EcDescriptorInfo>;
  dataSource: DescriptorsEcDataSource;
  tableData: EcDescriptorInfo[] = [];

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'name', 'version', 'ctxBlueprintId', 'ctxParameters'];

  constructor(private descriptorsEcService: DescriptorsEcService, private router: Router) {}

  ngOnInit() {
    this.dataSource = new DescriptorsEcDataSource(this.tableData);
    this.getEcDescriptors();
  }

  getEcDescriptors() {
    this.descriptorsEcService.getCtxDescriptors().subscribe((ecDescriptorsInfos: EcDescriptorInfo[]) => 
      {
        //console.log(ecDescriptorsInfos);
        this.tableData = ecDescriptorsInfos;
        this.dataSource = new DescriptorsEcDataSource(this.tableData);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }
}
