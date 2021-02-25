import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BlueprintsEDetailsComponent } from './blueprints-e-details.component';

describe('BlueprintsEDetailsComponent', () => {
  let component: BlueprintsEDetailsComponent;
  let fixture: ComponentFixture<BlueprintsEDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BlueprintsEDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BlueprintsEDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
