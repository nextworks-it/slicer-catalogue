import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ExperimentsMgmtDialogComponent } from './experiments-mgmt-dialog.component';

describe('ExperimentsMgmtDialogComponent', () => {
  let component: ExperimentsMgmtDialogComponent;
  let fixture: ComponentFixture<ExperimentsMgmtDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ExperimentsMgmtDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExperimentsMgmtDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
