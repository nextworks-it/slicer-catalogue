import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ExperimentsExecuteDialogComponent } from './experiments-execute-dialog.component';

describe('ExperimentsExecuteDialogComponent', () => {
  let component: ExperimentsExecuteDialogComponent;
  let fixture: ComponentFixture<ExperimentsExecuteDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ExperimentsExecuteDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExperimentsExecuteDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
