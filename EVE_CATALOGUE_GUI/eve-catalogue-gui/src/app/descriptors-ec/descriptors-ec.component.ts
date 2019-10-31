import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { DescriptorsEcDataSource } from './descriptors-ec-datasource';
import { EcDescriptorInfo } from './ec-descriptor-info';
import { DescriptorsEcService } from '../descriptors-ec.service';
import { Router } from '@angular/router';
import { BlueprintsEcService } from '../blueprints-ec.service';

@Component({
  selector: 'app-descriptors-ec',
  templateUrl: './descriptors-ec.component.html',
  styleUrls: ['./descriptors-ec.component.css']
})
export class DescriptorsEcComponent implements OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<EcDescriptorInfo>;
  dataSource: DescriptorsEcDataSource;
  tableData: EcDescriptorInfo[] = [];
  idToCtxbId: Map<string, Map<string, string>> = new Map();

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'name', 'version', 'ctxBlueprintId', 'ctxParameters'];

  constructor(private descriptorsEcService: DescriptorsEcService,
    private blueprintsCtxService: BlueprintsEcService,
    private router: Router) {}

  ngOnInit() {
    this.dataSource = new DescriptorsEcDataSource(this.tableData);
    this.getEcDescriptors();
  }

  getEcDescriptors() {
    this.descriptorsEcService.getCtxDescriptors().subscribe((ecDescriptorsInfos: EcDescriptorInfo[]) => 
      {
        //console.log(ecDescriptorsInfos);
        this.tableData = ecDescriptorsInfos;

        for (var i = 0; i < ecDescriptorsInfos.length; i ++) {
          this.idToCtxbId.set(ecDescriptorsInfos[i]['ctxDescriptorId'], new Map());
          this.getCtxBlueprint(ecDescriptorsInfos[i]['ctxDescriptorId'], ecDescriptorsInfos[i]['ctxBlueprintId']);
        }

        this.dataSource = new DescriptorsEcDataSource(this.tableData);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }

  getCtxBlueprint(ctxdId: string, ctxbId: string) {
    this.blueprintsCtxService.getCtxBlueprint(ctxbId).subscribe(ctxBlueprintInfo => {
      var names = this.idToCtxbId.get(ctxdId);
      names.set(ctxbId, ctxBlueprintInfo['name']);
    })
  }

  viewCtxBlueprint(ctxbId: string) {
    //console.log(ctxbId);
    localStorage.setItem('ctxbId', ctxbId);

    this.router.navigate(["/blueprints_ec_details"]);
  }
}
