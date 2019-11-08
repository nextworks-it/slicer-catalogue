import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ExperimentsDetailsComponent } from './experiments-details.component';

describe('ExperimentsDetailsComponent', () => {
  let component: ExperimentsDetailsComponent;
  let fixture: ComponentFixture<ExperimentsDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ExperimentsDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExperimentsDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
