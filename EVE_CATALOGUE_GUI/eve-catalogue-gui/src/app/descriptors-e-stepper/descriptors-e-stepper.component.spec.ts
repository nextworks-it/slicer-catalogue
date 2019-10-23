import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DescriptorsEStepperComponent } from './descriptors-e-stepper.component';

describe('DescriptorsEStepperComponent', () => {
  let component: DescriptorsEStepperComponent;
  let fixture: ComponentFixture<DescriptorsEStepperComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DescriptorsEStepperComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DescriptorsEStepperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
