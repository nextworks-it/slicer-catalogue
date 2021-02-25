import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BlueprintsGraphComponent } from './blueprints-graph.component';

describe('BlueprintsGraphComponent', () => {
  let component: BlueprintsGraphComponent;
  let fixture: ComponentFixture<BlueprintsGraphComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BlueprintsGraphComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BlueprintsGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
