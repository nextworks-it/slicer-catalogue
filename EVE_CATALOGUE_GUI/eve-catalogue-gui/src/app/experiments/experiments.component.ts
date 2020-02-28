import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { ExperimentInfo } from './experiment-info';
import { DescriptorsExpService } from '../descriptors-exp.service';
import { ExperimentsService } from '../experiments.service';
import { Router } from '@angular/router';
import { ExperimentsMgmtDialogComponent } from '../experiments-mgmt-dialog/experiments-mgmt-dialog.component';
import { ExperimentsExecuteDialogComponent } from '../experiments-execute-dialog/experiments-execute-dialog.component';
import { ExperimentsResultsDialogComponent } from '../experiments-results-dialog/experiments-results-dialog.component';

export interface DialogData {
  expId: string;
  expStatus: string;
  expExecutions: Object[];
}

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

  sites: string[] = [
    "ITALY_TURIN",
    "SPAIN_5TONIC",
    "FRANCE_PARIS",
    "FRANCE_NICE",
    "FRANCE_RENNES",
    "GREECE_ATHENS"
  ];

  tableData: ExperimentInfo[] = [
    /*{
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
    }*/
  ];

  dataSource = new MatTableDataSource(this.tableData);

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;

  idToExpdId: Map<string, Map<string, string>> = new Map();

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['experimentId', 'experimentDescriptorId', 'sites', 'status', 'execStatus', 'buttons'];

  constructor(private router: Router,
    public dialog: MatDialog,
    private descriptorsExpService: DescriptorsExpService,
    private experimentsService: ExperimentsService) { }

  ngOnInit() {
    this.dataSource = new MatTableDataSource(this.tableData);
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.getExperiments();
  }

  getExperiments() {
    this.experimentsService.getExperiments().subscribe((experimentInfos: ExperimentInfo[]) =>
      {
        //console.log(expDescriptorsInfos);
        this.tableData = experimentInfos;

        for (var i = 0; i < experimentInfos.length; i ++) {
          this.idToExpdId.set(experimentInfos[i].experimentId, new Map<string, string>());
          this.getExpDescriptor(experimentInfos[i].experimentId, experimentInfos[i].experimentDescriptorId);
        }
        this.dataSource = new MatTableDataSource(this.tableData);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
      });
  }

  getExpDescriptor(expId: string, expDId: string) {
    this.descriptorsExpService.getExpDescriptor(expDId).subscribe(expDescriptorInfo => {
      var names = this.idToExpdId.get(expId);
      names.set(expDId, expDescriptorInfo['name']);
    })
  }

  viewExpDescriptor(expDId: string) {
    localStorage.setItem('expdId', expDId);

    this.router.navigate(["/descriptors_e_details"]);
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

  deleteExperiment(expId: string) {
    this.experimentsService.deleteExperiment(expId).subscribe();
  }

  onSiteSelected(event: any) {
    var selectedSite = event.value;
    this.dataSource.filter = selectedSite.trim();
  }

  clearFilter() {
    this.dataSource.filter = '';
  }

  getRole() {
    return localStorage.getItem('role');
  }

  openMgmtDialog(expId: string, expStatus: string) {
    const dialogRef = this.dialog.open(ExperimentsMgmtDialogComponent, {
      width: '30%',
      data: {expId: expId, expStatus: expStatus, expExecutions: []}
    });

    dialogRef.afterClosed().subscribe(selectedStatus => {
      if (selectedStatus) {
        //console.log('Selected Status: ' + selectedStatus);
        var changeStatusRequest = {};
        changeStatusRequest['experimentId'] = expId;
        changeStatusRequest['status'] = selectedStatus;

        console.log('changeStatusRequest: ' + JSON.stringify(changeStatusRequest, null, 4));

        this.experimentsService.changeExperimentStatus(changeStatusRequest).subscribe();
      }
    });
  }

  openExecDialog(expId: string, expStatus: string) {
    const dialogRef = this.dialog.open(ExperimentsExecuteDialogComponent, {
      width: '30%',
      data: {expId: expId, expStatus: expStatus, expExecutions: []}
    });

    dialogRef.afterClosed().subscribe(formContent => {
      if (formContent) {
        //console.log('Selected Status: ' + selectedAction);
        var actionRequest = {};
        actionRequest['experimentId'] = expId;
        actionRequest['executionName'] = formContent.get('executionName').value;
        console.log('changeStatusRequest: ' + JSON.stringify(actionRequest, null, 4));

        this.experimentsService.executeExperimentAction(actionRequest, formContent.get('selectedAction').value).subscribe();
      }
    });
  }

  openResultsDialog(expId: string, expStatus: string, expExecutions: Object[]) {
    const dialogRef = this.dialog.open(ExperimentsResultsDialogComponent, {
      width: '30%',
      data: {expId: expId, expStatus: expStatus, expExecutions: expExecutions}
    });

    dialogRef.afterClosed().subscribe(selectedExecution => {
      if (selectedExecution) {
        console.log('Selected Execution: ' + selectedExecution);

        var resultsUrl = '';
        for (var i = 0; i < expExecutions.length; i++){
          if (expExecutions[i]['executionId'] == selectedExecution) {
            resultsUrl = expExecutions[i]['reportUrl'];
          }
        }

        window.open(resultsUrl, "_blank");
      }
    });
  }
}
