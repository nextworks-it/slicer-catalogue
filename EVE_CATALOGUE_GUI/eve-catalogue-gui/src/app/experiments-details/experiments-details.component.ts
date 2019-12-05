import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { ExperimentsDetailsDataSource, ExperimentsDetailsItemKV } from './experiments-details.datasource';
import { ExperimentsService } from '../experiments.service';
import { ExperimentInfo } from '../experiments/experiment-info';

@Component({
  selector: 'app-experiments-details',
  templateUrl: './experiments-details.component.html',
  styleUrls: ['./experiments-details.component.css']
})
export class ExperimentsDetailsComponent implements OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<ExperimentsDetailsItemKV>;
  dataSource: ExperimentsDetailsDataSource;

  tableData: ExperimentsDetailsItemKV[] = [];

  experiment: ExperimentInfo;

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['key', 'value'];

  constructor(private experimentsService: ExperimentsService) { }

  ngOnInit() {   
    var expId = localStorage.getItem('expId');
    this.dataSource = new ExperimentsDetailsDataSource(this.tableData);
    this.getExperiment(expId);
  }

  getExperiment(expId: string) {
    this.experimentsService.getExperiment(expId, null).subscribe((experimentInfos: ExperimentInfo[]) => {
      console.log(experimentInfos);
      this.experiment = experimentInfos[0];
      this.tableData.push({key: "Id", value: [this.experiment.experimentId]});
      if (this.getRole().indexOf('SITE_MANAGER') >= 0) {
        this.tableData.push({key: "Tenant Id", value: [this.experiment.tenantId]});
        this.tableData.push({key: "Ticket Id", value: [this.experiment.lcTicketId]});
        this.tableData.push({key: "Open Ticket Ids", value: this.experiment.openTicketIds});
      }      
      this.tableData.push({key: "Status", value: [this.experiment.status]});
      this.tableData.push({key: "Exp Descriptor Id", value: [this.experiment.experimentDescriptorId]});
      this.tableData.push({key: "Target Sites", value: this.experiment.targetSites});
      var startDate = new Date(0);
      startDate.setUTCSeconds(parseInt(this.experiment.timeslot.startTime));
      var stopDate = new Date(0);
      stopDate.setUTCSeconds(parseInt(this.experiment.timeslot.stopTime));
      this.tableData.push({key: "Time Slot", value: ['Start Date: ' + startDate.toLocaleString(), 'Stop Date: ' + stopDate.toLocaleString()]});
      this.tableData.push({key: "NFV Instance Id", value: [this.experiment.nfvNsInstanceId]});
      this.tableData.push({key: "Execution Id", value: [this.experiment.executionId]});
      this.tableData.push({key: "Execution Status", value: [this.experiment.executionStatus]});
      this.tableData.push({key: "Error Message", value: [this.experiment.errorMessage]});
      
      
      this.dataSource = new ExperimentsDetailsDataSource(this.tableData);
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
      this.table.dataSource = this.dataSource;
    });
  }

  getRole() {
    return localStorage.getItem('role');
  }
}
