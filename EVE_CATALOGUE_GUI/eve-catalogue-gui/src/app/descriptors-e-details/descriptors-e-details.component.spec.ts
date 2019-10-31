import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DescriptorsEDetailsComponent } from './descriptors-e-details.component';

describe('DescriptorsEDetailsComponent', () => {
  let component: DescriptorsEDetailsComponent;
  let fixture: ComponentFixture<DescriptorsEDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DescriptorsEDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DescriptorsEDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
