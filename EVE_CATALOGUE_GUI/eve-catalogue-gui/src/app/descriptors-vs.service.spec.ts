import { TestBed } from '@angular/core/testing';

import { DescriptorsVsService } from './descriptors-vs.service';

describe('DescriptorsVsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: DescriptorsVsService = TestBed.get(DescriptorsVsService);
    expect(service).toBeTruthy();
  });
});
