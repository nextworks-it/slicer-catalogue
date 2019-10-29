import { TestBed } from '@angular/core/testing';

import { DescriptorsTcService } from './descriptors-tc.service';

describe('DescriptorsTcService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: DescriptorsTcService = TestBed.get(DescriptorsTcService);
    expect(service).toBeTruthy();
  });
});
