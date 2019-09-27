import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';

import { NfvVnfComponent } from './nfv-vnf.component';

describe('NfvVnfComponent', () => {
  let component: NfvVnfComponent;
  let fixture: ComponentFixture<NfvVnfComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NfvVnfComponent ],
      imports: [
        NoopAnimationsModule,
        MatPaginatorModule,
        MatSortModule,
        MatTableModule,
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NfvVnfComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should compile', () => {
    expect(component).toBeTruthy();
  });
});
