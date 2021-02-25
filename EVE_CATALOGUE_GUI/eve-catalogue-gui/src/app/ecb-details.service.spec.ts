import { TestBed } from '@angular/core/testing';

import { EcbDetailsService } from './ecb-details.service';

describe('EcbDetailsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: EcbDetailsService = TestBed.get(EcbDetailsService);
    expect(service).toBeTruthy();
  });
});
