import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BlueprintsVsStepperComponent } from './blueprints-vs-stepper.component';

describe('BlueprintsVsStepperComponent', () => {
  let component: BlueprintsVsStepperComponent;
  let fixture: ComponentFixture<BlueprintsVsStepperComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BlueprintsVsStepperComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BlueprintsVsStepperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
