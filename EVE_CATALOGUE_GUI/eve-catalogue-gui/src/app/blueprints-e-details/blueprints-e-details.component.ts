import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { BlueprintsEDetailsDataSource, BlueprintsEDetailsItemKV } from './blueprints-e-details-datasource';
import { BlueprintsExpService } from '../blueprints-exp.service';
import { ExpBlueprintInfo } from '../blueprints-e/exp-blueprint-info';

@Component({
  selector: 'app-blueprints-e-details',
  templateUrl: './blueprints-e-details.component.html',
  styleUrls: ['./blueprints-e-details.component.css']
})
export class BlueprintsEDetailsComponent implements OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<BlueprintsEDetailsItemKV>;
  dataSource: BlueprintsEDetailsDataSource;

  tableData: BlueprintsEDetailsItemKV[] = [];

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  /**displayedColumns = ['expBlueprintId', 'name', 'activeExpdId', 'onBoardedNsdInfoId']; */
  displayedColumns = ['key', 'value'];

  constructor(private  blueprintsExpService: BlueprintsExpService) {}

  ngOnInit() {
    var expbId = localStorage.getItem('expbId');
    this.dataSource = new BlueprintsEDetailsDataSource(this.tableData);
    this.getExpBlueprint(expbId);
  }

  getExpBlueprint(expbId: string) {
    this.blueprintsExpService.getExpBlueprint(expbId).subscribe((expBlueprintInfo: ExpBlueprintInfo) => 
      {
        //console.log(vsBlueprintInfo);
        var expBlueprint = expBlueprintInfo['expBlueprint'];

        this.tableData.push({key: "Id", value: [expBlueprint['expBlueprintId']]});
        this.tableData.push({key: "Version", value: [expBlueprint['version']]});
        this.tableData.push({key: "Name", value: [expBlueprint['name']]});
        this.tableData.push({key: "Description", value: [expBlueprint['description']]});
        var values = [];

        for (var i = 0; i < expBlueprint['kpis'].length; i++) {
          values.push(expBlueprint['kpis'][i]['name']);
        }
        this.tableData.push({key: "KPIs", value: values});

        values = [];

        for (var i = 0; i < expBlueprint['metrics'].length; i++) {
          values.push(expBlueprint['metrics'][i]['name']);
        }
        this.tableData.push({key: "Metrics", value: values});

        values = [];

        for (var i = 0; i < expBlueprint['sites'].length; i++) {
          values.push(expBlueprint['sites'][i]);
        }
        this.tableData.push({key: "Compatible Sites", value: values});

        this.tableData.push({key: "Vertical Service", value: [expBlueprint['vsBlueprintId']]});

        values = [];

        for (var i = 0; i < expBlueprint['ctxBlueprintIds'].length; i++) {
          values.push(expBlueprint['ctxBlueprintIds'][i]);
        }
        this.tableData.push({key: "Execution Contexts", value: values});

        values = [];

        for (var i = 0; i < expBlueprint['tcBlueprintIds'].length; i++) {
          values.push(expBlueprint['tcBlueprintIds'][i]);
        }
        this.tableData.push({key: "Test Cases", value: values});

        values = [];

        for (var i = 0; i < expBlueprintInfo['activeExpdId'].length; i++) {
          values.push(expBlueprintInfo['activeExpdId'][i]);
        }
        this.tableData.push({key: "Active ExpDs", value: values});

        values = [];

        for (var i = 0; i < expBlueprintInfo['onBoardedNsdInfoId'].length; i++) {
          values.push(expBlueprintInfo['onBoardedNsdInfoId'][i]);
        }
        this.tableData.push({key: "Onboarded NSDs", value: values});

        this.dataSource = new BlueprintsEDetailsDataSource(this.tableData);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }
}
