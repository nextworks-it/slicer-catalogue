import { Component, OnInit, ViewChild } from '@angular/core';
import { VsbGraphService } from '../vsb-graph.service';
import { MatPaginator, MatSort, MatTable } from '@angular/material';
import { BlueprintsVsDetailsItemKV, BlueprintsVsDetailsDataSource } from './blueprints-vs-details.datasource';
import { VsBlueprintInfo } from '../blueprints-vs/vs-blueprint-info';
import { BlueprintsVsService } from '../blueprints-vs.service';

@Component({
  selector: 'app-blueprints-vs-details',
  templateUrl: './blueprints-vs-details.component.html',
  styles: [`
    app-blueprints-graph {
      height: 100vh;
      float: left;
      width: 100%;
      position: relative;
    }`]
})
export class BlueprintsVsDetailsComponent implements OnInit {

  node_name: string;

  @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<BlueprintsVsDetailsItemKV>;
  dataSource: BlueprintsVsDetailsDataSource;

  graphData = {
    nodes: [],
    edges: []
  };

  tableData: BlueprintsVsDetailsItemKV[] = [];

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['key', 'value'];

  constructor(private vsbGraphService: VsbGraphService, private  blueprintsVsService: BlueprintsVsService) {}

  ngOnInit() {
    var vsbId = localStorage.getItem('vsbId');
    this.dataSource = new BlueprintsVsDetailsDataSource(this.vsbGraphService._vsBlueprintDetailsItems);
    this.getVsBlueprint(vsbId);
  }

  nodeChange(event: any) {
      this.node_name = event;
  }

  getVsBlueprint(vsbId: string) {
    this.blueprintsVsService.getVsBlueprint(vsbId).subscribe((vsBlueprintInfo: VsBlueprintInfo) => 
      {
        //console.log(vsBlueprintInfo);
        var vsBlueprint = vsBlueprintInfo['vsBlueprint'];

        this.tableData.push({key: "Id", value: [vsBlueprint['blueprintId']]});
        this.tableData.push({key: "Version", value: [vsBlueprint['version']]});
        this.tableData.push({key: "Name", value: [vsBlueprint['name']]});
        this.tableData.push({key: "Description", value: [vsBlueprint['description']]});
        var values = [];

        for (var i = 0; i < vsBlueprint['parameters'].length; i++) {
          values.push(vsBlueprint['parameters'][i]['parameterName']);
        }
        this.tableData.push({key: "Parameters", value: values});

        values = [];

        var atomicComponents = vsBlueprint['atomicComponents'];
        for (var i = 0; i < atomicComponents.length; i++) {
          values.push(atomicComponents[i]['componentId']);
        }
        this.tableData.push({key: "Components", value: values});

        values = [];

        var endPoints = vsBlueprint['endPoints'];
        for (var i = 0; i < endPoints.length; i++) {
          values.push(endPoints[i]['endPointId']);
        }
        this.tableData.push({key: "Endpoints", value: values});

        values = [];

        for (var i = 0; i < vsBlueprint['compatibleContextBlueprint'].length; i++) {
          values.push(vsBlueprint['compatibleContextBlueprint'][i]);
        }
        this.tableData.push({key: "Context Blueprints", value: values});

        values = [];

        for (var i = 0; i < vsBlueprint['compatibleSites'].length; i++) {
          values.push(vsBlueprint['compatibleSites'][i]);
        }
        this.tableData.push({key: "Compatible Sites", value: values});

        values = [];

        for (var i = 0; i < vsBlueprintInfo['activeVsdId'].length; i++) {
          values.push(vsBlueprintInfo['activeVsdId'][i]);
        }
        this.tableData.push({key: "Active Vsds", value: values});
      
        for (var i = 0; i < atomicComponents.length; i++) {
          this.graphData.nodes.push(
            { data: { id: atomicComponents[i]['componentId'], name: atomicComponents[i]['componentId'], weight: 70, colorCode: 'white', shapeType: 'ellipse' }, classes: 'bottom-center vnf' }
          );
          this.graphData.edges.push(
            { data: { source: atomicComponents[i]['componentId'], target: atomicComponents[i]['endPointsIds'][0], colorCode: 'black', strength: 70 } }
          );
        }
        
        for (var i = 0; i < endPoints.length; i++) {
          if (endPoints[i]['ranConnection']) {
            this.graphData.nodes.push(
              { data: { id: endPoints[i]['endPointId'], name: endPoints[i]['endPointId'], weight: 50, colorCode: 'white', shapeType: 'ellipse' }, classes: 'bottom-center sap' }
            );
            for (var j = 0; j < endPoints.length; j++) {
              if (endPoints[i]['endPointId'] != endPoints[j]['endPointId']) {
                this.graphData.edges.push(
                  { data: { source: endPoints[i]['endPointId'], target: endPoints[j]['endPointId'], colorCode: 'grey', strength: 70 } }
                );
              }
            }
          }
          this.graphData.nodes.push(
            { data: { id: endPoints[i]['endPointId'], name: endPoints[i]['endPointId'], weight: 50, colorCode: 'white', shapeType: 'ellipse' }, classes: 'bottom-center net' }
          );
        }

        //console.log(this.tableData);
        //console.log(this.graphData);
        this.vsbGraphService.updateVSBTable(this.tableData);
        this.vsbGraphService.updateVSBGraph(this.graphData);
        this.dataSource = new BlueprintsVsDetailsDataSource(this.tableData);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }
}
