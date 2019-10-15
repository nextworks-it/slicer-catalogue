import { Component, OnInit } from '@angular/core';
import { VsbGraphService } from '../vsb-graph.service';

@Component({
  selector: 'app-vsb-graph',
  templateUrl: './vsb-graph.component.html',
  styles: [`
    app-blueprints-vs-graph {
      height: 100vh;
      float: left;
      width: 100%;
      position: relative;
    }`]
})
export class VsbGraphComponent implements OnInit {

  node_name: string;

  constructor(public vsbGraphService: VsbGraphService) {}

  ngOnInit() {}

  nodeChange(event: any) {
      this.node_name = event;
  }
}
