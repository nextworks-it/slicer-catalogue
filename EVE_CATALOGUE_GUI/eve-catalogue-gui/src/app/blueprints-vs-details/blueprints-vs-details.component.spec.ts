import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BlueprintsVsDetailsComponent } from './blueprints-vs-details.component';

describe('VsbGraphComponent', () => {
  let component: BlueprintsVsDetailsComponent;
  let fixture: ComponentFixture<BlueprintsVsDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BlueprintsVsDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BlueprintsVsDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
