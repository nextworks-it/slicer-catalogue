import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-sites-switch',
  templateUrl: './sites-switch.component.html',
  styleUrls: ['./sites-switch.component.css']
})
export class SitesSwitchComponent implements OnInit {

  cards = [
    { title: 'View Tickets', subtitle: '', cols: 1, rows: 1, path: '/tickets', icon: 'input' },
    { title: 'Manage Experiments', subtitle: '', cols: 1, rows: 1, path: '/manage_experiments', icon: 'input' } 
  ];

  constructor() { }

  ngOnInit() {
  }

}
