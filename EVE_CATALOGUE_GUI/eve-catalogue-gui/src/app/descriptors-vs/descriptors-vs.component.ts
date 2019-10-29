import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { DescriptorsVsDataSource } from './descriptors-vs-datasource';
import { VsDescriptorInfo } from './vs-descriptor-info';
import { DescriptorsVsService } from '../descriptors-vs.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-descriptors-vs',
  templateUrl: './descriptors-vs.component.html',
  styleUrls: ['./descriptors-vs.component.css']
})
export class DescriptorsVsComponent implements OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<VsDescriptorInfo>;
  dataSource: DescriptorsVsDataSource;
  tableData: VsDescriptorInfo[] = []

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'name', 'vsBlueprintId', 'sst', 'managementType'];

  constructor(private descriptorsVsService: DescriptorsVsService, private router: Router) {}

  ngOnInit() {
    this.dataSource = new DescriptorsVsDataSource(this.tableData);
    this.getVsDescriptors();
  }

  getVsDescriptors() {
    this.descriptorsVsService.getVsDescriptors().subscribe((vsDescriptorsInfos: VsDescriptorInfo[]) => 
      {
        //console.log(vsDescriptorsInfos);
        this.tableData = vsDescriptorsInfos;
        this.dataSource = new DescriptorsVsDataSource(this.tableData);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }
}
