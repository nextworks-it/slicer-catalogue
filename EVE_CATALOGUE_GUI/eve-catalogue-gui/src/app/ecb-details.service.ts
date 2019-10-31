import { Injectable } from '@angular/core';
import { BlueprintsCtxDetailsItemKV } from './blueprints-ec-details/blueprints-ec-details-datasource';

@Injectable({
  providedIn: 'root'
})
export class EcbDetailsService {

  _graphData: any = {
    nodes: [] = [],
    edges: [] = []
  };
  _ctxBlueprintDetailsItems: BlueprintsCtxDetailsItemKV[] = [];

  _ctxBlueprintId: string = "";

  public updateCTXBGraph(data: any) {
    this._graphData = data;
  }

  public updateCTXBTable(data: BlueprintsCtxDetailsItemKV[]) {
    //console.log(data);
    this._ctxBlueprintDetailsItems = data;
  }

  public updateCTXBId(data: string) {
    this._ctxBlueprintId = data;
    localStorage.setItem('ctxbId', data);
  }

  private addNode(node: any) {
    this._graphData.nodes.push(node);
  }

  private addEdge(edge: any) {
    this._graphData.edges.push(edge);
  }

  public get graphData() {
    return this._graphData;
  }

  public get CtxBlueprintDetailsItems() {
    return this._ctxBlueprintDetailsItems;
  }

  public get CtxBlueprintId() {
    return this._ctxBlueprintId;
  }
}
