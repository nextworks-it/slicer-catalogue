import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DescriptorsESchedulerComponent } from './descriptors-e-scheduler.component';

describe('DescriptorsESchedulerComponent', () => {
  let component: DescriptorsESchedulerComponent;
  let fixture: ComponentFixture<DescriptorsESchedulerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DescriptorsESchedulerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DescriptorsESchedulerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
