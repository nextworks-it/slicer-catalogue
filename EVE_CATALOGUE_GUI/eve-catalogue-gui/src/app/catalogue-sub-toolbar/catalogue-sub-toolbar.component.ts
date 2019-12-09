import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {Location} from '@angular/common';


@Component({
  selector: 'app-catalogue-sub-toolbar',
  templateUrl: './catalogue-sub-toolbar.component.html',
  styleUrls: ['./catalogue-sub-toolbar.component.css']
})
export class CatalogueSubToolbarComponent implements OnInit {

  constructor(private router: Router,
    private _location: Location) { }

  ngOnInit() {
  }

  goTo() {
    if (localStorage.getItem('role').indexOf('DESIGNER') >= 0) {
      this.router.navigate(['/design_experiment']);
    }

    if (localStorage.getItem('role').indexOf('EXPERIMENTER') >= 0) {
      this.router.navigate(['/request_experiment']);
    }
  }

  backClicked() {
    this._location.back();
  }

  getRole() {
    return localStorage.getItem('role');
  }
}
