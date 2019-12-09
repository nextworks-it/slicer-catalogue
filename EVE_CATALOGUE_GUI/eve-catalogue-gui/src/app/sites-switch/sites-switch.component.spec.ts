import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SitesSwitchComponent } from './sites-switch.component';

describe('SitesSwitchComponent', () => {
  let component: SitesSwitchComponent;
  let fixture: ComponentFixture<SitesSwitchComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SitesSwitchComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SitesSwitchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
