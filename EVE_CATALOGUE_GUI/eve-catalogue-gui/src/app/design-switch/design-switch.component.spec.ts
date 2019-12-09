import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DesignSwitchComponent } from './design-switch.component';

describe('DesignSwitchComponent', () => {
  let component: DesignSwitchComponent;
  let fixture: ComponentFixture<DesignSwitchComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DesignSwitchComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignSwitchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
