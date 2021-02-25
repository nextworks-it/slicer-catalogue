import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DescriptorsTcComponent } from './descriptors-tc.component';

describe('DescriptorsTcComponent', () => {
  let component: DescriptorsTcComponent;
  let fixture: ComponentFixture<DescriptorsTcComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DescriptorsTcComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DescriptorsTcComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
