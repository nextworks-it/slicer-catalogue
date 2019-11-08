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
    this.router.navigate([path]);
  }

}
