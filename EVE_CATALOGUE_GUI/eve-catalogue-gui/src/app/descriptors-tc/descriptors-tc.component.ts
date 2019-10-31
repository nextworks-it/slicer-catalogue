import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { DescriptorsTcDataSource } from './descriptors-tc-datasource';
import { TcDescriptorInfo } from './tc-descriptor-info';
import { DescriptorsTcService } from '../descriptors-tc.service';
import { Router } from '@angular/router';
import { BlueprintsTcService } from '../blueprints-tc.service';

@Component({
  selector: 'app-descriptors-tc',
  templateUrl: './descriptors-tc.component.html',
  styleUrls: ['./descriptors-tc.component.css']
})
export class DescriptorsTcComponent implements OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<TcDescriptorInfo>;
  dataSource: DescriptorsTcDataSource;
  tableData: TcDescriptorInfo[] = [];
  idToTcbId: Map<string, Map<string, string>> = new Map();

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'name', 'version', 'testCaseBlueprintId', 'userParameters', 'public'];

  constructor(private descriptorsTcService: DescriptorsTcService, 
    private blueprintsTcService: BlueprintsTcService,
    private router: Router) {}

  ngOnInit() {
    this.dataSource = new DescriptorsTcDataSource(this.tableData);
    this.getTestCaseDescriptors();
  }

  getTestCaseDescriptors() {
    this.descriptorsTcService.getTcDescriptors().subscribe((tcDescriptorsInfos: TcDescriptorInfo[]) => 
      {
        //console.log(tcDescriptorsInfos);
        this.tableData = tcDescriptorsInfos;

        for (var i = 0; i < tcDescriptorsInfos.length; i ++) {
          this.idToTcbId.set(tcDescriptorsInfos[i]['testCaseDescriptorId'], new Map());
          this.getTcBlueprint(tcDescriptorsInfos[i]['testCaseDescriptorId'], tcDescriptorsInfos[i]['testCaseBlueprintId']);
        } 

        this.dataSource = new DescriptorsTcDataSource(this.tableData);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }

  getTcBlueprint(tcdId: string, tcbId: string) {
    this.blueprintsTcService.getTcBlueprint(tcbId).subscribe(tcBlueprintInfo => {
      var names = this.idToTcbId.get(tcdId);
      names.set(tcbId, tcBlueprintInfo['name']);
    })
  }

  viewTcBlueprint(tcbId: string) {
    //console.log(tcbId);
    localStorage.setItem('tcbId', tcbId);

    this.router.navigate(["/blueprints_tc"]);
  }
}
