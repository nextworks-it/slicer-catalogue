import { TestBed } from '@angular/core/testing';

import { DescriptorsEcService } from './descriptors-ec.service';

describe('DescriptorsEcService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: DescriptorsEcService = TestBed.get(DescriptorsEcService);
    expect(service).toBeTruthy();
  });
});
