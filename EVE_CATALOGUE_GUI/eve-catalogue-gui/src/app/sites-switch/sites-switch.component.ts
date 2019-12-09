import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sites-switch',
  templateUrl: './sites-switch.component.html',
  styleUrls: ['./sites-switch.component.css']
})
export class SitesSwitchComponent implements OnInit {

  cards = [
    { title: 'View Tickets', subtitle: '', cols: 1, rows: 1, path: '/tickets', icon: '../../assets/images/format_list_bulleted_white.png', btn: '' },
    { title: 'Manage Experiments', subtitle: '', cols: 1, rows: 1, path: '/experiments', icon: '../../assets/images/build_white.png', btn: '' } 
  ];

  constructor(private router: Router) { }

  ngOnInit() {
  }

  goTo(path: string) {
    //console.log(expDescriptorId);
    localStorage.setItem('role', 'SITE_MANAGER');

    this.router.navigate([path]);
  }

}
