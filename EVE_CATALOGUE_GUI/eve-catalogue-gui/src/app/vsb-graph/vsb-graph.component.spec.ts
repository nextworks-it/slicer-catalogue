import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VsbGraphComponent } from './vsb-graph.component';

describe('VsbGraphComponent', () => {
  let component: VsbGraphComponent;
  let fixture: ComponentFixture<VsbGraphComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VsbGraphComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VsbGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
