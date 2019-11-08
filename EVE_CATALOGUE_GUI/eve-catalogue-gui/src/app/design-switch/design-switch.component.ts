import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-design-switch',
  templateUrl: './design-switch.component.html',
  styleUrls: ['./design-switch.component.css']
})
export class DesignSwitchComponent implements OnInit {

  cards = [
    /*{ title: 'Onboard Your VNF', subtitle: '', cols: 1, rows: 2, path: '' },*/
    { title: 'Onboard Execution Context', subtitle: '', cols: 1, rows: 1, path: '/blueprints_ec', icon: 'input' },
    { title: 'Onboard Test Case', subtitle: '', cols: 1, rows: 1, path: '/blueprints_tc', icon: 'input' },
    { title: 'Onboard VS Blueprint', subtitle: '', cols: 1, rows: 1, path: '/blueprints_vs', icon: 'input' },
    { title: 'Onboard Experiment Blueprint', subtitle: '', cols: 1, rows: 1, path: '/blueprints_exp', icon: 'input' },
    { title: 'Navigate 5G EVE Catalogue', subtitle: '', cols: 2, rows: 1, path: '/dashboard', icon: 'menu_open' }  
  ];

  constructor() { }

  ngOnInit() {
  }

}
