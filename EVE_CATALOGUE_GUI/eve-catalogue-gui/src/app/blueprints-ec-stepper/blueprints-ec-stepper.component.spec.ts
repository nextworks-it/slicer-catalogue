import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BlueprintsEcStepperComponent } from './blueprints-ec-stepper.component';

describe('BlueprintsEcStepperComponent', () => {
  let component: BlueprintsEcStepperComponent;
  let fixture: ComponentFixture<BlueprintsEcStepperComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BlueprintsEcStepperComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BlueprintsEcStepperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
