import { Component, OnInit, ViewChild, AfterViewInit, Input } from '@angular/core';
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
  displayedColumns = ['id', 'expDId', 'sites', 'status', 'execStatus', 'buttons'];

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

  /**
   * Paginate the data (client-side). If you're using server-side pagination,
   * this would be replaced by requesting the appropriate data from the server.
   */
  private getPagedData(data: ExperimentInfo[]) {
    const startIndex = this.paginator.pageIndex * this.paginator.pageSize;
    return data.splice(startIndex, this.paginator.pageSize);
  }

  /**
   * Sort the data (client-side). If you're using server-side sorting,
   * this would be replaced by requesting the appropriate data from the server.
   */
  private getSortedData(data: ExperimentInfo[]) {
    if (!this.sort.active || this.sort.direction === '') {
      return data;
    }

    return data.sort((a, b) => {
      const isAsc = this.sort.direction === 'asc';
      switch (this.sort.active) {
        case 'id': return this.compare(+a.experimentId, +b.experimentId, isAsc);
        default: return 0;
      }
    });
  }

  /** Simple sort comparator for example ID/Name columns (for client-side sorting). */
  compare(a, b, isAsc) {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
  }
}
