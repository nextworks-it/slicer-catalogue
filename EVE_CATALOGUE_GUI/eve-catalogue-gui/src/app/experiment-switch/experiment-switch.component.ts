import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../environments/environments';

@Component({
  selector: 'app-experiment-switch',
  templateUrl: './experiment-switch.component.html',
  styleUrls: ['./experiment-switch.component.css']
})
export class ExperimentSwitchComponent implements OnInit {

  ibnBaseUrl = environment['ibnBaseUrl'];

  cards = [
    { title: 'Experiment Descriptors', subtitle: 'Create', cols: 1, rows: 1, path: '/descriptors_exp', icon: '../../assets/images/content_copy_white.png', btn: 'input'},
    { title: 'New Experiment', subtitle: 'Schedule', cols: 1, rows: 1, path: '/schedule_experiment', icon: '../../assets/images/layers_white.png', btn: 'event_note' },
    { title: 'Intent Declaration', subtitle: '', cols: 1, rows: 1, path: this.ibnBaseUrl, icon: '../../assets/images/lightbulb_outline_white.png', btn: '' }
  ];

  constructor(private router: Router) { }

  ngOnInit() {
  }

  openExtLink() {
    window.open(this.ibnBaseUrl);
  }

  goTo(path: string) {
    if (path.indexOf('http') >= 0) {
      window.open(path, '_blank');
    } else {
      this.router.navigate([path]);
    }
  }

}
