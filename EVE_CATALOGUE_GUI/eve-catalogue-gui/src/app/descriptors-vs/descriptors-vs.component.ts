import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { DescriptorsVsDataSource } from './descriptors-vs-datasource';
import { VsDescriptorInfo } from './vs-descriptor-info';
import { DescriptorsVsService } from '../descriptors-vs.service';
import { Router } from '@angular/router';
import { BlueprintsVsService } from '../blueprints-vs.service';

@Component({
  selector: 'app-descriptors-vs',
  templateUrl: './descriptors-vs.component.html',
  styleUrls: ['./descriptors-vs.component.css']
})
export class DescriptorsVsComponent implements OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<VsDescriptorInfo>;
  dataSource: DescriptorsVsDataSource;
  tableData: VsDescriptorInfo[] = [];
  idToVsbId: Map<string, Map<string, string>> = new Map();

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'name', 'vsBlueprintId', 'sst', 'managementType', 'buttons'];

  constructor(private descriptorsVsService: DescriptorsVsService,
    private blueprintsVsService: BlueprintsVsService,
    private router: Router) {}

  ngOnInit() {
    this.dataSource = new DescriptorsVsDataSource(this.tableData);
    this.getVsDescriptors();
  }

  getVsDescriptors() {
    this.descriptorsVsService.getVsDescriptors().subscribe((vsDescriptorsInfos: VsDescriptorInfo[]) =>
      {
        //console.log(vsDescriptorsInfos);
        this.tableData = vsDescriptorsInfos;

        for (var i = 0; i < vsDescriptorsInfos.length; i ++) {
          this.idToVsbId.set(vsDescriptorsInfos[i]['vsDescriptorId'], new Map());
          this.getVsBlueprint(vsDescriptorsInfos[i]['vsDescriptorId'], vsDescriptorsInfos[i]['vsBlueprintId']);
        }

        this.dataSource = new DescriptorsVsDataSource(this.tableData);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }

  getVsBlueprint(vsdId: string, vsbId: string) {
    this.blueprintsVsService.getVsBlueprint(vsbId).subscribe(vsBlueprintInfo => {
      var names = this.idToVsbId.get(vsdId);
      names.set(vsbId, vsBlueprintInfo['name']);
    })
  }

  viewVsBlueprint(vsbId: string) {
    //console.log(vsbId);
    localStorage.setItem('vsbId', vsbId);

    this.router.navigate(["/blueprints_vs_details"]);
  }

  viewVsDescriptor(vsDescriptorId: string) {
    //console.log(vsDescriptorId);
    localStorage.setItem('vsdId', vsDescriptorId);

    this.router.navigate(["/descriptors_vs_details"]);
  }
}
