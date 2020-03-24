import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { BlueprintsEDetailsDataSource, BlueprintsEDetailsItemKV } from './blueprints-e-details-datasource';
import { BlueprintsExpService } from '../blueprints-exp.service';
import { ExpBlueprintInfo } from '../blueprints-e/exp-blueprint-info';
import { CtxBlueprintInfo } from '../blueprints-ec/ctx-blueprint-info';
import { TcBlueprintInfo } from '../blueprints-tc/tc-blueprint-info';
import { BlueprintsEcService } from '../blueprints-ec.service';
import { BlueprintsTcService } from '../blueprints-tc.service';
import { BlueprintsVsService} from '../blueprints-vs.service';
import { VsBlueprintInfo } from '../blueprints-vs/vs-blueprint-info';

@Component({
  selector: 'app-blueprints-e-details',
  templateUrl: './blueprints-e-details.component.html',
  styleUrls: ['./blueprints-e-details.component.css']
})
export class BlueprintsEDetailsComponent implements OnInit {
  [x: string]: any;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<BlueprintsEDetailsItemKV>;
  dataSource: BlueprintsEDetailsDataSource;

  tableData: BlueprintsEDetailsItemKV[] = [];

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['key', 'value'];

  vsBlueprintsList: VsBlueprintInfo[] = [];
  ctxBlueprintsList: CtxBlueprintInfo[] = [];
  tcBlueprintsList: TcBlueprintInfo[] = [];

  constructor(
    private blueprintsExpService: BlueprintsExpService,
    private vsBlueprintService: BlueprintsVsService,
    private ctxBlueprintService: BlueprintsEcService,
    private blueprintsTcService: BlueprintsTcService
    ) {}

  ngOnInit() {
    var expbId = localStorage.getItem('expbId');
    this.dataSource = new BlueprintsEDetailsDataSource(this.tableData);
    this.getExpBlueprint(expbId);
  }

  getExpBlueprint(expbId: string) {
    this.blueprintsExpService.getExpBlueprint(expbId).subscribe((expBlueprintInfo: ExpBlueprintInfo) => {
      this.vsBlueprintService.getVsBlueprints().subscribe((vsBlueprintsList: VsBlueprintInfo[]) => {
        this.ctxBlueprintService.getCtxBlueprints().subscribe((ctxBlueprintsList: CtxBlueprintInfo[]) => {
          this.blueprintsTcService.getTcBlueprints().subscribe((tcBlueprintsList: TcBlueprintInfo[]) => {
        //console.log(expBlueprintInfo);
        var expBlueprint = expBlueprintInfo['expBlueprint'];

        this.tableData.push({key: "Name", value: [expBlueprint['name']]});
        this.tableData.push({key: "Id", value: [expBlueprint['expBlueprintId']]});
        this.tableData.push({key: "Version", value: [expBlueprint['version']]});
        this.tableData.push({key: "Description", value: [expBlueprint['description']]});
        var values = [];

        if (expBlueprint['kpis']) {
          for (var i = 0; i < expBlueprint['kpis'].length; i++) {
            values.push(expBlueprint['kpis'][i]['name']);
          }
          this.tableData.push({key: "KPIs", value: values});
        }

        if (expBlueprint['metrics']) {
          values = [];

          for (var i = 0; i < expBlueprint['metrics'].length; i++) {
            values.push(expBlueprint['metrics'][i]['name']);
          }
          this.tableData.push({key: "Metrics", value: values});
        }

        values = [];

        for (var i = 0; i < expBlueprint['sites'].length; i++) {
          values.push(expBlueprint['sites'][i]);
        }
        this.tableData.push({key: "Compatible Sites", value: values});

        for (var i = 0; i < vsBlueprintsList.length; i++){
          if (vsBlueprintsList[i]['vsBlueprintId'] === expBlueprint['vsBlueprintId']){
            this.tableData.push({key: "Vertical Service", value: [vsBlueprintsList[i]['name']]});
          }
        }


        values = [];

        for (var i = 0; i < expBlueprint['ctxBlueprintIds'].length; i++) {
          for (var j = 0; j < ctxBlueprintsList.length; j++){
            if (ctxBlueprintsList[j]['ctxBlueprintId'] === expBlueprint['ctxBlueprintIds'][i]){
              values.push(ctxBlueprintsList[j]['name']);
            }
          }
        }
        this.tableData.push({key: "Execution Contexts", value: values});

        values = [];

        for (var i = 0; i < expBlueprint['tcBlueprintIds'].length; i++) {
          for(var j = 0; j < tcBlueprintsList.length; j++){
            if (expBlueprint['tcBlueprintIds'][i] === tcBlueprintsList[j]['testCaseBlueprintId']){
              values.push(tcBlueprintsList[j]['name']);
            }
          }

        }
        this.tableData.push({key: "Test Cases", value: values});

/*        values = [];

        for (var i = 0; i < expBlueprintInfo['activeExpdId'].length; i++) {
          values.push(expBlueprintInfo['activeExpdId'][i]);
        }
        this.tableData.push({key: "Active ExpDs", value: values});
*/
        values = [];

        for (var i = 0; i < expBlueprintInfo['onBoardedNsdInfoId'].length; i++) {
          values.push(expBlueprintInfo['onBoardedNsdInfoId'][i]);
        }
        this.tableData.push({key: "Onboarded NSDs", value: values});

        this.dataSource = new BlueprintsEDetailsDataSource(this.tableData);
        this.dataSource.sort = this.sort;
        this.table.dataSource = this.dataSource;
      });
    });
  });
});
  }
}
