import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {Location} from '@angular/common';

@Component({
  selector: 'app-experiment-sub-toolbar',
  templateUrl: './experiment-sub-toolbar.component.html',
  styleUrls: ['./experiment-sub-toolbar.component.css']
})
export class ExperimentSubToolbarComponent implements OnInit {

  constructor(private router: Router,
    private _location: Location) { }

  ngOnInit() {
  }

  goTo() {
    if (localStorage.getItem('role').indexOf('SITE_MANAGER') >= 0) {
      this.router.navigate(['/manage_site']);
    }

    if (localStorage.getItem('role').indexOf('EXPERIMENTER') >= 0) {
      this.router.navigate(['/experiments']);
    }
  }

  backClicked() {
    this._location.back();
  }

  getRole() {
    return localStorage.getItem('role');
  }
}
