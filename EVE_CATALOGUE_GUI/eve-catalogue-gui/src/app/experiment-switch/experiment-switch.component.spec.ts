import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ExperimentSwitchComponent } from './experiment-switch.component';

describe('ExperimentSwitchComponent', () => {
  let component: ExperimentSwitchComponent;
  let fixture: ComponentFixture<ExperimentSwitchComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ExperimentSwitchComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExperimentSwitchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
