import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class VsbGraphService {
  _graphData: any = {
    nodes: [] = [],
    edges: [] = []
  }

  processVSB(data: any) {
    this._graphData = data;
  }

  private addNode(node: any) {
    this._graphData.nodes.push(node);
  }

  private addEdge(edge: any) {
    this._graphData.edges.push(edge);
  }

  get graphData() {
    return this._graphData;
  }
}
