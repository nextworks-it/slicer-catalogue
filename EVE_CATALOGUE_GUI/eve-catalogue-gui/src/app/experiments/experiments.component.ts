import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { ExperimentDataSource } from './experiment.datasource';
import { ExperimentInfo } from './experiment-info';
import { Router } from '@angular/router';

@Component({
  selector: 'app-experiments',
  templateUrl: './experiments.component.html',
  styleUrls: ['./experiments.component.css']
})
export class ExperimentsComponent implements OnInit, AfterViewInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<ExperimentInfo>;
  dataSource: ExperimentDataSource;
  tableData: ExperimentInfo[] = [
    {
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
    }
  ];

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'expDId', 'sites', 'status', 'execStatus', 'buttons'];

  constructor() { }

  ngOnInit() {
    this.dataSource = new ExperimentDataSource(this.tableData);
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.table.dataSource = this.dataSource;
  }
}
