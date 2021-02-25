import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { DescriptorsVsDetailsDataSource, DescriptorsVsDetailsItemKV } from './descriptors-vs-details.datasource';
import { DescriptorsVsService } from '../descriptors-vs.service';
import { VsDescriptorInfo } from '../descriptors-vs/vs-descriptor-info';

@Component({
  selector: 'app-descriptors-vs-details',
  templateUrl: './descriptors-vs-details.component.html',
  styleUrls: ['./descriptors-vs-details.component.css']
})
export class DescriptorsVsDetailsComponent implements OnInit {

  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<DescriptorsVsDetailsItemKV>;
  dataSource: DescriptorsVsDetailsDataSource;

  tableData: DescriptorsVsDetailsItemKV[] = [];

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['key', 'value'];

  constructor(private  descriptorsVsService: DescriptorsVsService) {}

  ngOnInit() {
    var vsdId = localStorage.getItem('vsdId');
    this.dataSource = new DescriptorsVsDetailsDataSource(this.tableData);
    this.getVsDescriptor(vsdId);
  }

  getVsDescriptor(vsdId: string) {
    this.descriptorsVsService.getVsDescriptor(vsdId).subscribe((vsDescriptorInfo: VsDescriptorInfo) => 
      {
        //console.log(vsDescriptorInfo);

        this.tableData.push({key: "Id", value: [vsDescriptorInfo['vsDescriptorId']]});
        this.tableData.push({key: "Version", value: [vsDescriptorInfo['version']]});
        this.tableData.push({key: "Name", value: [vsDescriptorInfo['name']]});
        this.tableData.push({key: "VS Blueprint Id", value: [vsDescriptorInfo['vsBlueprintId']]});
        this.tableData.push({key: "Service Slice Type", value: [vsDescriptorInfo['sst']]});
        this.tableData.push({key: "Management Type", value: [vsDescriptorInfo['managementType']]});
        var values = [];

        var qosParameters = new Map(Object.entries(vsDescriptorInfo['qosParameters']));
        qosParameters.forEach((value: string, key: string) => {
          values.push(key + ": " + value)
        });
        this.tableData.push({key: "QoS", value: values});

        this.tableData.push({key: "Service Creation Time", value: [vsDescriptorInfo['sla']['serviceCreationTime']]});
        this.tableData.push({key: "Availability Coverage", value: [vsDescriptorInfo['sla']['availabilityCoverage']]});
        this.tableData.push({key: "Low Cost", value: [vsDescriptorInfo['sla']['lowCostRequired']]});

        this.dataSource = new DescriptorsVsDetailsDataSource(this.tableData);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }
}
