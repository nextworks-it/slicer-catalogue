import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { BlueprintsVsDataSource } from './blueprints-vs-datasource';
import { BlueprintsVsService } from '../blueprints-vs.service';
import { VsBlueprintInfo } from './vs-blueprint-info';
import { VsbDetailsService } from  '../vsb-details.service';
import { DescriptorsVsService } from '../descriptors-vs.service';

@Component({
  selector: 'app-blueprints-vs',
  templateUrl: './blueprints-vs.component.html',
  styleUrls: ['./blueprints-vs.component.css']
})
export class BlueprintsVsComponent implements OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<VsBlueprintInfo>;
  dataSource: BlueprintsVsDataSource;
  vsBlueprintInfos: VsBlueprintInfo[] = [];
  idToVsdIds: Map<string, Map<string, string>> = new Map();

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
//  displayedColumns = ['id', 'name', 'version', 'description', 'conf_params', 'vsds', 'buttons'];
  displayedColumns = ['id', 'name', 'version', 'description', 'conf_params', 'buttons'];

  constructor(private blueprintsVsService: BlueprintsVsService,
    private vsbDetailsService: VsbDetailsService,
    private descriptorsVsService: DescriptorsVsService,
    private router: Router) { }

  ngOnInit() {
    this.dataSource = new BlueprintsVsDataSource(this.vsBlueprintInfos);
    this.getVsBlueprints();
  }

  getVsBlueprints(): void {
    this.blueprintsVsService.getVsBlueprints().subscribe((vsBlueprintInfos: VsBlueprintInfo[]) =>
      {
        //console.log(vsBlueprintInfos);
        this.vsBlueprintInfos = vsBlueprintInfos;

        for (var i = 0; i < vsBlueprintInfos.length; i++) {
          this.idToVsdIds.set(vsBlueprintInfos[i]['vsBlueprintId'], new Map());
          for (var j = 0; j < vsBlueprintInfos[i]['activeVsdId'].length; j++) {
            this.getVsDescriptor(vsBlueprintInfos[i]['vsBlueprintId'], vsBlueprintInfos[i]['activeVsdId'][j]);
          }
        }
        this.dataSource = new BlueprintsVsDataSource(this.vsBlueprintInfos);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }

  getVsDescriptor(vsbId: string, vsdId: string) {
    this.descriptorsVsService.getVsDescriptor(vsdId).subscribe(vsDescriptorInfo => {
      var names = this.idToVsdIds.get(vsbId);
      names.set(vsdId, vsDescriptorInfo['name']);
    })
  }

  viewVsDescriptor(vsdId: string) {
    //console.log(vsdId);
    localStorage.setItem('vsdId', vsdId);

    this.router.navigate(["/descriptors_vs_details"]);
  }

  deleteVsBlueprint(vsBlueprintId: string) {
    //console.log(vsBlueprintId);
    this.blueprintsVsService.deleteVsBlueprint(vsBlueprintId).subscribe();
  }

  viewVsBlueprintGraph(vsBlueprintId: string) {
    //console.log(vsBlueprintId);
    this.vsbDetailsService.updateVSBId(vsBlueprintId);

    //routerLink="/blueprints_vs_graph"
    this.router.navigate(["/blueprints_vs_details"]);
  }
}
