import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-side-top-bar',
  templateUrl: './side-top-bar.component.html',
  styleUrls: ['./side-top-bar.component.css']
})
export class SideTopBarComponent implements OnInit {

  logged: boolean;

  btns: any[] = [];
  footerInfo=[
    {"title":"5G EVE Portal",
    "link":"https://portal.5g-eve.eu",
    "context":"Portal to design, manage and monitor experiments over the 5G EVE end-to-end facility infrastructures."},
    {"title":"5G EVE Wiki",
    "link":"http://wiki.5g-eve.eu",
    "context":"Documentation for experimenters and developers operating on 5G EVE."},
    {"title":"Report Issues",
    "link":"https://portal.5g-eve.eu/bugzilla",
    "context":"Issues tracker for 5G EVE experimenters."}
  ];

  ngOnInit(): void {
    if (localStorage.getItem('logged') && localStorage.getItem('logged') == 'true') {
      this.logged = true;
    } else {
      this.logged = false;
    }
  }

  constructor() {}

}
