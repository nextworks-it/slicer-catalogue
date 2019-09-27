import { Component } from '@angular/core';
import { map } from 'rxjs/operators';
import { Breakpoints, BreakpointObserver } from '@angular/cdk/layout';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
  cards = [
    { title: 'Vertical Service Blueprints', subtitle: '', counter: 3, cols: 1, rows: 1 },
    { title: 'Vertical Service Descriptors', subtitle: '', counter: 9, cols: 1, rows: 1 },
    { title: 'Execution Context Blueprints', subtitle: '', counter: 5, cols: 1, rows: 1 },
    { title: 'Execution Context Descriptors', subtitle: '', counter: 9, cols: 1, rows: 1 },
    { title: 'Test Case Blueprints', subtitle: '', counter: 3, cols: 1, rows: 1 },
    { title: 'Test Case Descriptors', subtitle: '', counter: 3, cols: 1, rows: 1 },    
    { title: 'Experiments Blueprints', subtitle: '', counter: 3, cols: 1, rows: 1 },
    { title: 'Experiments Descriptors', subtitle: '', counter: 9, cols: 1, rows: 1 }/*,
    { title: 'Network Service Descriptors', subtitle: '', cols: 1, rows: 1 },
    { title: 'Virtual Network Function Packages', subtitle: '', cols: 1, rows: 1 },
    { title: 'Physical Network Function Descriptors', subtitle: '', cols: 1, rows: 1 }*/
  ];

  constructor(private breakpointObserver: BreakpointObserver) {}
}
