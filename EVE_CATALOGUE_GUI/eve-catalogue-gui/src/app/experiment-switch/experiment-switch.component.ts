import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-experiment-switch',
  templateUrl: './experiment-switch.component.html',
  styleUrls: ['./experiment-switch.component.css']
})
export class ExperimentSwitchComponent implements OnInit {

  cards = [
    { title: 'Experiment Descriptors', subtitle: 'Create', cols: 1, rows: 1, path: '/descriptors_exp', icon: '../../assets/images/content_copy_white.png', btn: 'input'},
    { title: 'New Experiment', subtitle: 'Schedule', cols: 1, rows: 1, path: '/schedule_experiment', icon: '../../assets/images/layers_white.png', btn: 'event_note' } 
  ];

  constructor() { }

  ngOnInit() {
  }

}
