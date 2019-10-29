import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { DescriptorsEDataSource } from './descriptors-e-datasource';
import { ExpDescriptorInfo } from './exp-descriptor-info';
import { DescriptorsExpService } from '../descriptors-exp.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-descriptors-e',
  templateUrl: './descriptors-e.component.html',
  styleUrls: ['./descriptors-e.component.css']
})
export class DescriptorsEComponent implements OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<ExpDescriptorInfo>;
  dataSource: DescriptorsEDataSource;
  tableData: ExpDescriptorInfo[] = []

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'name', 'expElueprintId', 'vsDescriptorId', 'kpi', 'buttons'];

  constructor(private descriptorsExpService: DescriptorsExpService, private router: Router) { }

  ngOnInit() {
    this.dataSource = new DescriptorsEDataSource(this.tableData);
    this.getExpDescriptors();
  }

  getExpDescriptors() {
    this.descriptorsExpService.getExpDescriptors().subscribe((expDescriptorsInfos: ExpDescriptorInfo[]) => 
      {
        //console.log(expDescriptorsInfos);
        this.tableData = expDescriptorsInfos;
        this.dataSource = new DescriptorsEDataSource(this.tableData);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }

  deleteExpDescriptor(expDescriptorId: string) {
    //console.log(expDescriptorId);
    this.descriptorsExpService.deleteExpDescriptor(expDescriptorId).subscribe();
  }
}
