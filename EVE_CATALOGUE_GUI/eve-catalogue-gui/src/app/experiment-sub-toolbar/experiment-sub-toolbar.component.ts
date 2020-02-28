import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { UsersService, User } from '../users.service';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-experiment-sub-toolbar',
  templateUrl: './experiment-sub-toolbar.component.html',
  styleUrls: ['./experiment-sub-toolbar.component.css']
})
export class ExperimentSubToolbarComponent implements OnInit {

  username: string = '';

  constructor(private router: Router,
    private _location: Location,
    private usersService: UsersService,
    private authService: AuthService) { }

  ngOnInit() {
    this.username = localStorage.getItem('username');
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

  logout() {
    this.authService.logout('/login').subscribe(tokenInfo => console.log(JSON.stringify(tokenInfo, null, 4)));
  }
}
