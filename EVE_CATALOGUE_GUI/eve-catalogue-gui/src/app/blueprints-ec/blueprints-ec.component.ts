import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { BlueprintsEcDataSource } from './blueprints-ec-datasource';
import { BlueprintsEcService } from '../blueprints-ec.service';
import { Router } from '@angular/router';
import { CtxBlueprintInfo } from './ctx-blueprint-info';
import { EcbDetailsService } from '../ecb-details.service';
import { DescriptorsEcService } from '../descriptors-ec.service';

@Component({
  selector: 'app-blueprints-ec',
  templateUrl: './blueprints-ec.component.html',
  styleUrls: ['./blueprints-ec.component.css']
})
export class BlueprintsEcComponent implements OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<CtxBlueprintInfo>;
  dataSource: BlueprintsEcDataSource;
  ctxBlueprintInfos: CtxBlueprintInfo[] = [];
  idToCtxdIds: Map<string, Map<string, string>> = new Map();

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
//  displayedColumns = ['id', 'name', 'version', 'description', 'conf_params', 'ctxds', 'buttons'];
  displayedColumns = ['id', 'name', 'version', 'description', 'conf_params', 'buttons'];

  constructor(private blueprintsEcService: BlueprintsEcService,
    private ecbDetailsService: EcbDetailsService,
    private descriptorsEcService: DescriptorsEcService,
    private router: Router) { }

  ngOnInit() {
    this.dataSource = new BlueprintsEcDataSource(this.ctxBlueprintInfos);
    this.getEcBlueprints();
  }

  getEcBlueprints(): void {
    this.blueprintsEcService.getCtxBlueprints().subscribe((ctxBlueprintInfos: CtxBlueprintInfo[]) =>
      {
        //console.log(ctxBlueprintInfos);
        this.ctxBlueprintInfos = ctxBlueprintInfos;

        for (var i = 0; i < ctxBlueprintInfos.length; i++) {
          this.idToCtxdIds.set(ctxBlueprintInfos[i]['ctxBlueprintId'], new Map());
          for (var j = 0; j < ctxBlueprintInfos[i]['activeCtxdId'].length; j++) {
            this.getCtxDescriptor(ctxBlueprintInfos[i]['ctxBlueprintId'], ctxBlueprintInfos[i]['activeCtxdId'][j]);
          }
        }
        this.dataSource = new BlueprintsEcDataSource(this.ctxBlueprintInfos);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }

  getCtxDescriptor(ctxbId: string, ctxdId: string) {
    this.descriptorsEcService.getCtxDescriptor(ctxdId).subscribe(ctxDescriptorInfo => {
      var names = this.idToCtxdIds.get(ctxbId);
      names.set(ctxdId, ctxDescriptorInfo['name']);
    })
  }

  viewCtxDescriptor(ctxdId: string) {
    //console.log(vsdId);
    localStorage.setItem('ctxdId', ctxdId);

    this.router.navigate(["/descriptors_ec"]);
  }

  deleteEcBlueprint(ctxBlueprintId: string) {
    //console.log(ctxBlueprintId);
    this.blueprintsEcService.deleteCtxBlueprint(ctxBlueprintId).subscribe();
  }

  viewEcBlueprintGraph(ctxBlueprintId: string) {
    //console.log(ctxBlueprintId);
    this.ecbDetailsService.updateCTXBId(ctxBlueprintId);

    this.router.navigate(["/blueprints_ec_details"]);
  }
}
