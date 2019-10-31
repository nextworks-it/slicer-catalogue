import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DescriptorsVsDetailsComponent } from './descriptors-vs-details.component';

describe('DescriptorsVsDetailsComponent', () => {
  let component: DescriptorsVsDetailsComponent;
  let fixture: ComponentFixture<DescriptorsVsDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DescriptorsVsDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DescriptorsVsDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
