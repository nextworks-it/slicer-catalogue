import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { BlueprintsVsDataSource } from './blueprints-vs-datasource';
import { BlueprintsVsService } from '../blueprints-vs.service';
import { VsBlueprintInfo } from './vs-blueprint-info';
import { VsbGraphService } from  '../vsb-graph.service';

@Component({
  selector: 'app-blueprints-vs',
  templateUrl: './blueprints-vs.component.html',
  styleUrls: ['./blueprints-vs.component.css']
})
export class BlueprintsVsComponent implements /*AfterViewInit,*/ OnInit {
  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<VsBlueprintInfo>;
  dataSource: BlueprintsVsDataSource;
  vsBlueprintInfos: VsBlueprintInfo[] = [];
  graphData = {
    nodes: [
      /*{ data: { id: 'a', name: 'Signup', weight: 100, colorCode: 'blue', shapeType: 'roundrectangle' } },
      { data: { id: 'b', name: 'User Profile', weight: 100, colorCode: 'magenta', shapeType: 'roundrectangle' } },
      { data: { id: 'c', name: 'Billing', weight: 100, colorCode: 'magenta', shapeType: 'roundrectangle' } },
      { data: { id: 'd', name: 'Sales', weight: 100, colorCode: 'orange', shapeType: 'roundrectangle' } },
      { data: { id: 'e', name: 'Referral', weight: 100, colorCode: 'orange', shapeType: 'roundrectangle' } },
      { data: { id: 'f', name: 'Loan', weight: 100, colorCode: 'orange', shapeType: 'roundrectangle' } },
      { data: { id: 'j', name: 'Support', weight: 100, colorCode: 'red', shapeType: 'ellipse' } },
      { data: { id: 'k', name: 'Sink Event', weight: 100, colorCode: 'green', shapeType: 'ellipse' } }*/
    ],
    edges: [
      /*{ data: { source: 'a', target: 'b', colorCode: 'blue', strength: 10 } },
      { data: { source: 'b', target: 'c', colorCode: 'blue', strength: 10 } },
      { data: { source: 'c', target: 'd', colorCode: 'blue', strength: 10 } },
      { data: { source: 'c', target: 'e', colorCode: 'blue', strength: 10 } },
      { data: { source: 'c', target: 'f', colorCode: 'blue', strength: 10 } },
      { data: { source: 'e', target: 'j', colorCode: 'red', strength: 10 } },
      { data: { source: 'e', target: 'k', colorCode: 'green', strength: 10 } }*/
    ]
  };

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['id', 'name', 'version', 'description', 'conf_params', 'vsds', 'buttons'];

  constructor(private blueprintsVsService: BlueprintsVsService, private vsbGraphService: VsbGraphService) { }

  ngOnInit() {
    this.dataSource = new BlueprintsVsDataSource(this.vsBlueprintInfos);
    this.getVsBlueprints();
  }

  /*ngAfterViewInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.table.dataSource = this.dataSource;
  }*/

  getVsBlueprints(): void {
    this.blueprintsVsService.getVsBlueprints().subscribe((vsBlueprintInfos: VsBlueprintInfo[]) => 
      {
        //console.log(vsBlueprintInfos);
        this.vsBlueprintInfos = vsBlueprintInfos;
        this.dataSource = new BlueprintsVsDataSource(this.vsBlueprintInfos);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }

  deleteVsBlueprint(vsBlueprintId: string) {
    this.blueprintsVsService.deleteVsBlueprint(vsBlueprintId).subscribe();
  }

  viewVsBlueprintGraph(vsBlueprintId: string) {
    this.blueprintsVsService.getVsBlueprint().subscribe((vsBlueprintInfo: VsBlueprintInfo) => 
      {
        console.log(vsBlueprintInfo);
        var vsBlueprint = vsBlueprintInfo[0]['vsBlueprint'];
        var atomicComponents = vsBlueprint['atomicComponents'];
        for (var i = 0; i < atomicComponents.length; i++) {
          this.graphData.nodes.push(
            { data: { id: atomicComponents[i]['componentId'], name: atomicComponents[i]['componentId'], weight: 100, colorCode: 'blue', shapeType: 'roundrectangle' } }
          );
          this.graphData.edges.push(
            { data: { source: atomicComponents[i]['componentId'], target: atomicComponents[i]['endPointsIds'][0], colorCode: 'blue', strength: 10 } }
          );
        }
        var endPoints = vsBlueprint['endPoints'];
        for (var i = 0; i < endPoints.length; i++) {
          this.graphData.nodes.push(
            { data: { id: endPoints[i]['endPointId'], name: endPoints[i]['endPointId'], weight: 100, colorCode: 'blue', shapeType: 'ellipse' } }
          );
        }
        this.vsbGraphService.processVSB(this.graphData);
      });
  }
}
