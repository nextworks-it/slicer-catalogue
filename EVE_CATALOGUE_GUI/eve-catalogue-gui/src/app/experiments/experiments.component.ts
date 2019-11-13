import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ExperimentInfo } from './experiment-info';
import { Router } from '@angular/router';

@Component({
  selector: 'app-experiments',
  templateUrl: './experiments.component.html',
  styleUrls: ['./experiments.component.css']
})
export class ExperimentsComponent implements OnInit {

  states: string[] = [
    "SCHEDULING",
    "ACCEPTED",
    "READY",
    "INSTANTIATING",
    "INSTANTIATED",
    "CONFIGURING",
    "RUNNING",
    "TERMINATING",
    "TERMINATED",
    "FAILED",
    "REFUSED",
    "ABORTED"
  ];

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
    },
    {
      experimentId: 'experimentId_2',
      tenantId: 'tenantId_2',
      status: 'INSTANTIATED',
      experimentDescriptorId: '13',
      lcTicketId: '24',
      openTicketIds: ['17'],
      targetSites: ['ITALY_TURIN'],
      timeslot: {
          startTime: 'today',
          stopTime: 'tomorrow'
      },
      nfvNsInstanceId: '48',
      executionId: '57',
      executionStatus: 'INIT',
      errorMessage: '!!!!!!!'
    }
  ];

  dataSource = new MatTableDataSource(this.tableData);

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['experimentId', 'experimentDescriptorId', 'sites', 'status', 'execStatus', 'buttons'];

  constructor(private router: Router) { }

  ngOnInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator; 
  }

  viewExperiment(expId: string) {
    //console.log(expId);
    localStorage.setItem('expId', expId);

    this.router.navigate(["/experiments_details"]);
  }

  onStatusSelected(event: any) {
    var selectedState = event.value;
    this.dataSource.filter = selectedState.trim();   
  }

  clearStateFilter() {    
    this.dataSource.filter = '';
  }
}