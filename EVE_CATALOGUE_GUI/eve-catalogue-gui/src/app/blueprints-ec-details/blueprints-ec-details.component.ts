import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSort, MatTable } from '@angular/material';
import { BlueprintsCtxDetailsItemKV, BlueprintsEcDetailsDataSource } from './blueprints-ec-details-datasource';
import { BlueprintsEcService } from '../blueprints-ec.service';
import { EcbDetailsService } from '../ecb-details.service';
import { CtxBlueprintInfo } from '../blueprints-ec/ctx-blueprint-info';

@Component({
  selector: 'app-blueprints-ec-details',
  templateUrl: './blueprints-ec-details.component.html',
  styles: [`
    app-blueprints-graph {
      height: 70vh;
      float: left;
      width: 100%;
      position: relative;
    }`]
})
export class BlueprintsEcDetailsComponent implements OnInit {

  node_name: string;

  @ViewChild(MatSort, {static: false}) sort: MatSort;
  @ViewChild(MatTable, {static: false}) table: MatTable<BlueprintsCtxDetailsItemKV>;
  dataSource: BlueprintsEcDetailsDataSource;

  graphData = {
    nodes: [],
    edges: []
  };

  tableData: BlueprintsCtxDetailsItemKV[] = [];

  /** Columns displayed in the table. Columns IDs can be added, removed, or reordered. */
  displayedColumns = ['key', 'value'];

  constructor(private ecbDetailsService: EcbDetailsService, private  blueprintsEcService: BlueprintsEcService) {}

  ngOnInit() {
    var ctxbId = localStorage.getItem('ctxbId');
    this.dataSource = new BlueprintsEcDetailsDataSource(this.ecbDetailsService._ctxBlueprintDetailsItems);
    this.getCtxBlueprint(ctxbId);
  }

  nodeChange(event: any) {
    this.node_name = event;
  }

  getCtxBlueprint(ctxbId: string) {
    this.blueprintsEcService.getCtxBlueprint(ctxbId).subscribe((ctxBlueprintInfo: CtxBlueprintInfo) =>
      {
        //console.log(vsBlueprintInfo);
        var ctxBlueprint = ctxBlueprintInfo['ctxBlueprint'];
        this.tableData.push({key: "Name", value: [ctxBlueprint['name']]});
        this.tableData.push({key: "Id", value: [ctxBlueprint['blueprintId']]});
        this.tableData.push({key: "Version", value: [ctxBlueprint['version']]});
        this.tableData.push({key: "Description", value: [ctxBlueprint['description']]});
        var values = [];

        for (var i = 0; i < ctxBlueprint['parameters'].length; i++) {
          values.push(ctxBlueprint['parameters'][i]['parameterName']);
        }
        this.tableData.push({key: "Configuration options", value: values});

        values = [];

        var atomicComponents = ctxBlueprint['atomicComponents'];
        for (var i = 0; i < atomicComponents.length; i++) {
          values.push(atomicComponents[i]['componentId']);
        }
        this.tableData.push({key: "Components", value: values});

        values = [];

        var endPoints = ctxBlueprint['endPoints'];
        for (var i = 0; i < endPoints.length; i++) {
          values.push(endPoints[i]['endPointId']);
        }
        this.tableData.push({key: "Endpoints", value: values});

        values = [];

        for (var i = 0; i < ctxBlueprint['compatibleSites'].length; i++) {
          values.push(ctxBlueprint['compatibleSites'][i]);
        }
        this.tableData.push({key: "Compatible Sites", value: values});
/*
        values = [];

        for (var i = 0; i < ctxBlueprintInfo['activeCtxdId'].length; i++) {
          values.push(ctxBlueprintInfo['activeCtxdId'][i]);
        }
        this.tableData.push({key: "Active Ctxds", value: values});
*/
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

        var connectivityServices = ctxBlueprint['connectivityServices'];

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
        this.ecbDetailsService.updateCTXBTable(this.tableData);
        this.ecbDetailsService.updateCTXBGraph(this.graphData);
        this.dataSource = new BlueprintsEcDetailsDataSource(this.tableData);
        this.dataSource.sort = this.sort;
        this.table.dataSource = this.dataSource;
      });
  }
}
