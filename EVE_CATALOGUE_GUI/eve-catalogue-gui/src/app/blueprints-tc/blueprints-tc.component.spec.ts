import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BlueprintsTcComponent } from './blueprints-tc.component';

describe('BlueprintsTcComponent', () => {
  let component: BlueprintsTcComponent;
  let fixture: ComponentFixture<BlueprintsTcComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BlueprintsTcComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BlueprintsTcComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
