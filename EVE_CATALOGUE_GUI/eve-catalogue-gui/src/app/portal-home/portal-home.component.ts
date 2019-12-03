import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-portal-home',
  templateUrl: './portal-home.component.html',
  styleUrls: ['./portal-home.component.css']
})
export class PortalHomeComponent implements OnInit {

  constructor(private router: Router) { }

  ngOnInit() {
  }

  goTo(path: string) {
    if (path.indexOf('/design_experiment') >= 0) {
      localStorage.setItem('role', 'DESIGNER');
    }
    if (path.indexOf('/request_experiment') >= 0) {
      localStorage.setItem('role', 'EXPERIMENTER');
    }
    if (path.indexOf('/experiments') >= 0) {
      localStorage.setItem('role', 'EXPERIMENTER');
    }
    if (path.indexOf('/manage_site') >= 0) {
      localStorage.setItem('role', 'SITE_MANAGER');
    }
    this.router.navigate([path]);
  }

}
