import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BlueprintsEcDetailsComponent } from './blueprints-ec-details.component';

describe('BlueprintsEcDetailsComponent', () => {
  let component: BlueprintsEcDetailsComponent;
  let fixture: ComponentFixture<BlueprintsEcDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BlueprintsEcDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BlueprintsEcDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
