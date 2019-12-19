import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Breakpoints, BreakpointObserver } from '@angular/cdk/layout';
import { DomSanitizer } from '@angular/platform-browser';
import { MatIconRegistry } from '@angular/material/icon';
import { BlueprintsEcService } from '../blueprints-ec.service';
import { BlueprintsExpService } from '../blueprints-exp.service';
import { BlueprintsTcService } from '../blueprints-tc.service';
import { BlueprintsVsService } from '../blueprints-vs.service';
import { DescriptorsEcService } from '../descriptors-ec.service';
import { DescriptorsExpService } from '../descriptors-exp.service';
import { DescriptorsTcService } from '../descriptors-tc.service';
import { DescriptorsVsService } from '../descriptors-vs.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  cards = [
    /*,
    { title: 'Network Service Descriptors', subtitle: '', cols: 1, rows: 1 },
    { title: 'Virtual Network Function Packages', subtitle: '', cols: 1, rows: 1 },
    { title: 'Physical Network Function Descriptors', subtitle: '', cols: 1, rows: 1 }*/
  ];

  constructor(private breakpointObserver: BreakpointObserver,
    iconRegistry: MatIconRegistry, sanitizer: DomSanitizer,
    private router: Router,
    private blueprintsEcService: BlueprintsEcService,
    private blueprintsExpService: BlueprintsExpService,
    private bluepritnsTcService: BlueprintsTcService,
    private blueprintsVsService: BlueprintsVsService,
    private descriptorsEcService: DescriptorsEcService,
    private descriptorsExpService: DescriptorsExpService,
    private descriptorsTcService: DescriptorsTcService,
    private descriptorsVsService: DescriptorsVsService
    ) {
      iconRegistry.addSvgIcon(
        'library_add',
        sanitizer.bypassSecurityTrustResourceUrl('assets/images/library_add.svg'));
    }

    ngOnInit(): void {
      this.getVsBlueprints();
      //this.getVsDescriptors();
      this.getEcBlueprints();
      //this.getEcDescriptors();
      this.getTcBlueprints();
      //this.getTcDescriptors();
      this.getExpBlueprints();
      //this.getExpDescriptors();
    }

    getEcBlueprints() {
      this.blueprintsEcService.getCtxBlueprints().subscribe(ctxBlueprints => {
        this.cards.push({ title: 'Execution Context Blueprints', subtitle: '', counter: ctxBlueprints.length, cols: 1, rows: 1, path: '/blueprints_ec' })
      });
    }

    getExpBlueprints() {
      this.blueprintsExpService.getExpBlueprints().subscribe(expBlueprints => {
        this.cards.push({ title: 'Experiments Blueprints', subtitle: '', counter: expBlueprints.length, cols: 1, rows: 1, path: '/blueprints_exp' });
        this.cards.push({ title: 'Virtual Network Functions', subtitle: '', counter: '', cols: 1, rows: 1, path: '' });
      });
    }

    getTcBlueprints() {
      this.bluepritnsTcService.getTcBlueprints().subscribe(tcBlueprints => {
        this.cards.push({ title: 'Test Case Blueprints', subtitle: '', counter: tcBlueprints.length, cols: 1, rows: 1, path: '/blueprints_tc' });
      });
    }

    getVsBlueprints() {
      this.blueprintsVsService.getVsBlueprints().subscribe(vsBlueprints => {
        this.cards.push({ title: 'Vertical Service Blueprints', subtitle: '', counter: vsBlueprints.length, cols: 1, rows: 1, path: '/blueprints_vs' });
      });
    }

    getEcDescriptors(){
      this.descriptorsEcService.getCtxDescriptors().subscribe(ctxDescriptors => {
        this.cards.push({ title: 'Execution Context Descriptors', subtitle: '', counter: ctxDescriptors.length, cols: 1, rows: 1, path: '/descriptors_ec' })
      });
    }

    getExpDescriptors() {
      this.descriptorsExpService.getExpDescriptors().subscribe(expDescriptors => {
        this.cards.push({ title: 'Experiments Descriptors', subtitle: '', counter: expDescriptors.length, cols: 1, rows: 1, path: '/descriptors_exp' })
      });
    }

    getTcDescriptors() {
      this.descriptorsTcService.getTcDescriptors().subscribe(tcDescriptors => {
        this.cards.push({ title: 'Test Case Descriptors', subtitle: '', counter: tcDescriptors.length, cols: 1, rows: 1, path: '/descriptors_tc' })
      });
    }

    getVsDescriptors() {
      this.descriptorsVsService.getVsDescriptors().subscribe(vsDescriptors => {
        this.cards.push({ title: 'Vertical Service Descriptors', subtitle: '', counter: vsDescriptors.length, cols: 1, rows: 1, path: '/descriptors_vs' })
      });
    }

    goTo(path: string) {
      if (path.indexOf('http') >= 0) {
        window.open(path, '_blank');
      } else {
        this.router.navigate([path]);
      }
    }
}
