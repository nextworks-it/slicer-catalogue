import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-experiment-switch',
  templateUrl: './experiment-switch.component.html',
  styleUrls: ['./experiment-switch.component.css']
})
export class ExperimentSwitchComponent implements OnInit {

  cards = [
    { title: 'Define Experiment Descriptor', subtitle: '', cols: 1, rows: 1, path: '/descriptors_exp', icon: 'input' },
    { title: 'Schedule a new Experiment', subtitle: '', cols: 1, rows: 1, path: '/schedule_experiment', icon: 'input' } 
  ];

  constructor() { }

  ngOnInit() {
  }

}
