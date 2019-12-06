import { Component, OnInit, ViewChild } from '@angular/core';
import { VsbDetailsService } from '../vsb-details.service';
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

  constructor(private vsbDetailservice: VsbDetailsService, private  blueprintsVsService: BlueprintsVsService) {}

  ngOnInit() {
    var vsbId = localStorage.getItem('vsbId');
    this.dataSource = new BlueprintsVsDetailsDataSource(this.vsbDetailservice._vsBlueprintDetailsItems);
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

        var atomicComponentsCps = [];
      
        for (var i = 0; i < atomicComponents.length; i++) {
          this.graphData.nodes.push(
            { data: { id: atomicComponents[i]['componentId'], name: atomicComponents[i]['componentId'], weight: 70, colorCode: 'white', shapeType: 'ellipse' }, classes: 'bottom-center vnf' }
          );

          atomicComponentsCps.push(...atomicComponents[i]['endPointsIds']);
          console.log(atomicComponentsCps);
        }

        var sapCps = [];
        for (var i = 0; i < endPoints.length; i ++) {
          if (!(atomicComponentsCps.includes(endPoints[i]['endPointId']))) {
            sapCps.push(endPoints[i]['endPointId']);
            this.graphData.nodes.push(
              { data: { id: endPoints[i]['endPointId'], name: endPoints[i]['endPointId'], weight: 50, colorCode: 'white', shapeType: 'ellipse' }, classes: 'bottom-center sap' }
            );
          }
        }
        
        var connectivityServices = vsBlueprint['connectivityServices'];

        for (var i = 0; i < connectivityServices.length; i++) {
          this.graphData.nodes.push(
            { data: { id: "conn_service_" + i, name: "", weight: 50, colorCode: 'white', shapeType: 'ellipse' }, classes: 'bottom-center net' }
          );

          for (var j = 0; j < atomicComponents.length; j++) {
            for (var h = 0; h < atomicComponents[j]['endPointsIds'].length; h++) {
              if (connectivityServices[i]['endPointIds'].includes(atomicComponents[j]['endPointsIds'][h])) {
                this.graphData.edges.push(
                  { data: { source: atomicComponents[j]['componentId'], target: "conn_service_" + i, colorCode: 'black', strength: 70 } }
                );
              }
            }
          }
          
          for (var j = 0; j < sapCps.length; j++) {
            if (connectivityServices[i]['endPointIds'].includes(sapCps[j])) {
              this.graphData.edges.push(
                { data: { source: sapCps[j], target: "conn_service_" + i, colorCode: 'grey', strength: 70 } }
              );
            }
          }
        }

        //console.log(this.tableData);
        //console.log(this.graphData);
        this.vsbDetailservice.updateVSBTable(this.tableData);
        this.vsbDetailservice.updateVSBGraph(this.graphData);
        this.dataSource = new BlueprintsVsDetailsDataSource(this.tableData);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.table.dataSource = this.dataSource;
      });
  }
}
