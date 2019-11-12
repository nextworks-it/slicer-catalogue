import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { ExperimentsDetailsDataSource, ExperimentsDetailsItemKV } from './experiments-details.datasource';
import { ExperimentInfo } from '../experiments/experiment-info';

@Component({
  selector: 'app-experiments-details',
  templateUrl: './experiments-details.component.html',
  styleUrls: ['./experiments-details.component.css']
})
export class ExperimentsDetailsComponent implements OnInit, AfterViewInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<ExperimentsDetailsItemKV>;
  dataSource: ExperimentsDetailsDataSource;

  tableData: ExperimentsDetailsItemKV[] = [];

  experiment: ExperimentInfo = {
    experimentId: 'experimentId',
    tenantId: 'tenantId',
    status: 'ACCEPTED',
    experimentDescriptorId: '12',
    lcTicketId: '23',
    openTicketIds: ['16'],
    targetSites: ['ITALY_TURIN'],
    timeslot: {
        startTime: 'today',
        stopTime: 'tomorrow'
    },
    nfvNsInstanceId: '47',
    executionId: '56',
    executionStatus: 'INIT',
    errorMessage: '!!!!!!!'
  };

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['key', 'value'];

  constructor() { }

  ngOnInit() {
    var expId = localStorage.getItem('expId');this.tableData.push({key: "Id", value: ['experimentId']});
    this.tableData.push({key: "Tenant Id", value: ['tenantId']});
    this.tableData.push({key: "Status", value: ['ACCEPTED']});
    this.tableData.push({key: "Exp Descriptor Id", value: ['12']});
    this.tableData.push({key: "Ticket Id", value: ['23']});
    this.tableData.push({key: "Open Ticket Ids", value: ['16']});
    this.tableData.push({key: "Target Sites", value: ['ITALY_TURIN']});
    this.tableData.push({key: "Time Slot", value: ['today -> tomorrow']});
    this.tableData.push({key: "NFV Instance Id", value: ['47']});
    this.tableData.push({key: "Execution Id", value: ['56']});
    this.tableData.push({key: "Execution Status", value: ['INIT']});
    this.tableData.push({key: "Error Message", value: ['!!!!!!!']});
    
    this.dataSource = new ExperimentsDetailsDataSource(this.tableData);
    //this.getExperiment(expId);
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.table.dataSource = this.dataSource;
  }

  getExperiment(expId: string) {
    this.tableData.push({key: "Id", value: ['experimentId']});
    this.tableData.push({key: "Tenant Id", value: ['tenantId']});
    this.tableData.push({key: "Status", value: ['ACCEPTED']});
    this.tableData.push({key: "Exp Descriptor Id", value: ['12']});
    this.tableData.push({key: "Ticket Id", value: ['23']});
    this.tableData.push({key: "Open Ticket Ids", value: ['16']});
    this.tableData.push({key: "Target Sites", value: ['ITALY_TURIN']});
    this.tableData.push({key: "Time Slot", value: ['today -> tomorrow']});
    this.tableData.push({key: "NFV Instance Id", value: ['47']});
    this.tableData.push({key: "Execution Id", value: ['56']});
    this.tableData.push({key: "Execution Status", value: ['INIT']});
    this.tableData.push({key: "Error Message", value: ['!!!!!!!']});
    
    
    this.dataSource = new ExperimentsDetailsDataSource(this.tableData);
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.table.dataSource = this.dataSource;
  }
}
