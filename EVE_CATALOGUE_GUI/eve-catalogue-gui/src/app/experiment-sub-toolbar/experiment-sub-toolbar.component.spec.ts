import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ExperimentSubToolbarComponent } from './experiment-sub-toolbar.component';

describe('ExperimentSubToolbarComponent', () => {
  let component: ExperimentSubToolbarComponent;
  let fixture: ComponentFixture<ExperimentSubToolbarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ExperimentSubToolbarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExperimentSubToolbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
