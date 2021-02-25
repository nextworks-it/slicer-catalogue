import { Component, OnInit } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { VERSION } from '@angular/material';

@Component({
  selector: 'app-side-top-bar',
  templateUrl: './side-top-bar.component.html',
  styleUrls: ['./side-top-bar.component.css']
})
export class SideTopBarComponent implements OnInit {

  version = VERSION;
  btns: any[] = [];

  ngOnInit(): void {
    for (let i = 1; i <= 10; i++) {
      const item = { id: i, name: `Person ${i}`, email: `person${i}@gmail.com` };

      this.btns.push(item);
    }
  }

  onOpenMenu(menu: any): void {
    console.log(menu);
  }

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    );

  constructor(private breakpointObserver: BreakpointObserver) {}

}
