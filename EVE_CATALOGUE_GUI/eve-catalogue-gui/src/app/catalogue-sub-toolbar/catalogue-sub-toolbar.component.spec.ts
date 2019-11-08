import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CatalogueSubToolbarComponent } from './catalogue-sub-toolbar.component';

describe('CatalogueSubToolbarComponent', () => {
  let component: CatalogueSubToolbarComponent;
  let fixture: ComponentFixture<CatalogueSubToolbarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CatalogueSubToolbarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CatalogueSubToolbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
