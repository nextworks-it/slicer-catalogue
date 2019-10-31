import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { DescriptorsEDetailsDataSource, DescriptorsEDetailsItemKV } from './descriptors-e-details.datasource';
import { DescriptorsExpService } from '../descriptors-exp.service';
import { ExpDescriptorInfo } from '../descriptors-e/exp-descriptor-info';

@Component({
  selector: 'app-descriptors-e-details',
  templateUrl: './descriptors-e-details.component.html',
  styleUrls: ['./descriptors-e-details.component.css']
})
export class DescriptorsEDetailsComponent implements OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<DescriptorsEDetailsItemKV>;
  dataSource: DescriptorsEDetailsDataSource;

  tableData: DescriptorsEDetailsItemKV[] = [];

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['key', 'value'];

  constructor(private  descriptorsExpService: DescriptorsExpService) {}

  ngOnInit() {
    var expdId = localStorage.getItem('expdId');
    this.dataSource = new DescriptorsEDetailsDataSource(this.tableData);
    this.getExpDescriptor(expdId);
  }

  getExpDescriptor(expdId: string) {
    this.descriptorsExpService.getExpDescriptor(expdId).subscribe((expDescriptorInfo: ExpDescriptorInfo) => 
      {
        //console.log(vsDescriptorInfo);

        this.tableData.push({key: "Id", value: [expDescriptorInfo['expDescriptorId']]});
        this.tableData.push({key: "Version", value: [expDescriptorInfo['version']]});
        this.tableData.push({key: "Name", value: [expDescriptorInfo['name']]});
        this.tableData.push({key: "Exp Blueprint Id", value: [expDescriptorInfo['expBlueprintId']]});
        this.tableData.push({key: "VS Descriptor Id", value: [expDescriptorInfo['vsDescriptorId']]});
        this.tableData.push({key: "Ctx Descriptor Ids", value: expDescriptorInfo['ctxDescriptorIds']});
        this.tableData.push({key: "TC Descriptor Ids", value: expDescriptorInfo['testCaseDescriptorIds']});
        var values = [];

        var kpis = new Map(Object.entries(expDescriptorInfo['kpiThresholds']));
        kpis.forEach((value: string, key: string) => {
          values.push(key + ": " + value)
        });
        this.tableData.push({key: "KPIs Thresholds", value: values});
        
        this.dataSource = new DescriptorsEDetailsDataSource(this.tableData);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }
}
