import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { DescriptorsEDataSource } from './descriptors-e-datasource';
import { ExpDescriptorInfo } from './exp-descriptor-info';
import { DescriptorsExpService } from '../descriptors-exp.service';
import { Router } from '@angular/router';
import { BlueprintsExpService } from '../blueprints-exp.service';
import { DescriptorsVsService } from '../descriptors-vs.service';

@Component({
  selector: 'app-descriptors-e',
  templateUrl: './descriptors-e.component.html',
  styleUrls: ['./descriptors-e.component.css']
})
export class DescriptorsEComponent implements OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<ExpDescriptorInfo>;
  dataSource: DescriptorsEDataSource;
  tableData: ExpDescriptorInfo[] = [];
  idToExpbId: Map<string, Map<string, string>> = new Map();
  idToVsdId: Map<string, Map<string, string>> = new Map();

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'name', 'expElueprintId', 'vsDescriptorId', 'kpi', 'buttons'];

  constructor(private descriptorsExpService: DescriptorsExpService, 
    private blueprintsExpService: BlueprintsExpService,
    private descriptorsVsService: DescriptorsVsService,
    private router: Router) { }

  ngOnInit() {
    this.dataSource = new DescriptorsEDataSource(this.tableData);
    this.getExpDescriptors();
  }

  getExpDescriptors() {
    this.descriptorsExpService.getExpDescriptors().subscribe((expDescriptorsInfos: ExpDescriptorInfo[]) => 
      {
        //console.log(expDescriptorsInfos);
        this.tableData = expDescriptorsInfos;

        for (var i = 0; i < expDescriptorsInfos.length; i ++) {
          this.idToExpbId.set(expDescriptorsInfos[i]['expDescriptorId'], new Map());
          this.idToVsdId.set(expDescriptorsInfos[i]['expDescriptorId'], new Map());
          this.getExpBlueprint(expDescriptorsInfos[i]['expDescriptorId'], expDescriptorsInfos[i]['expBlueprintId']);
          this.getVsDescriptor(expDescriptorsInfos[i]['expDescriptorId'], expDescriptorsInfos[i]['vsDescriptorId']);
        } 

        this.dataSource = new DescriptorsEDataSource(this.tableData);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }

  getExpBlueprint(expdId: string, expbId: string) {
    this.blueprintsExpService.getExpBlueprint(expbId).subscribe(expBlueprintInfo => {
      var names = this.idToExpbId.get(expdId);
      names.set(expbId, expBlueprintInfo['name']);
    })
  }

  viewExpBlueprint(expbId: string) {
    //console.log(expbId);
    localStorage.setItem('expbId', expbId);

    this.router.navigate(["/blueprints_e_details"]);
  }

  getVsDescriptor(expdId: string, vsdId: string) {
    this.descriptorsVsService.getVsDescriptor(vsdId).subscribe(vsDescriptorInfo => {
      var names = this.idToVsdId.get(expdId);
      names.set(vsdId, vsDescriptorInfo['name']);
    })
  }

  viewVsDescriptor(vsdId: string) {
    //console.log(vsdId);
    localStorage.setItem('vsdId', vsdId);

    this.router.navigate(["/descriptors_vs_details"]);
  }

  deleteExpDescriptor(expDescriptorId: string) {
    //console.log(expDescriptorId);
    this.descriptorsExpService.deleteExpDescriptor(expDescriptorId).subscribe();
  }

  viewExpDescriptor(expDescriptorId) {
    //console.log(expDescriptorId);
    localStorage.setItem('expdId', expDescriptorId);

    this.router.navigate(["/descriptors_e_details"]);
  }
}
