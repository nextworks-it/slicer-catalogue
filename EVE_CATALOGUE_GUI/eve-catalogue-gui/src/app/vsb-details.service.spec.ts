import { TestBed } from '@angular/core/testing';

import { VsbDetailsService } from './vsb-details.service';

describe('VsbDetailsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: VsbDetailsService = TestBed.get(VsbDetailsService);
    expect(service).toBeTruthy();
  });
});
