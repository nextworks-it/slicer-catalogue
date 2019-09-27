import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BlueprintsVsGraphComponent } from './blueprints-vs-graph.component';

describe('BlueprintsVsGraphComponent', () => {
  let component: BlueprintsVsGraphComponent;
  let fixture: ComponentFixture<BlueprintsVsGraphComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BlueprintsVsGraphComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BlueprintsVsGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
