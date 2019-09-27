import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BlueprintsEStepperComponent } from './blueprints-e-stepper.component';

describe('BlueprintsEStepperComponent', () => {
  let component: BlueprintsEStepperComponent;
  let fixture: ComponentFixture<BlueprintsEStepperComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BlueprintsEStepperComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BlueprintsEStepperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
