import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ExperimentsResultsDialogComponent } from './experiments-results-dialog.component';

describe('ExperimentsResultsDialogComponent', () => {
  let component: ExperimentsResultsDialogComponent;
  let fixture: ComponentFixture<ExperimentsResultsDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ExperimentsResultsDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExperimentsResultsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
