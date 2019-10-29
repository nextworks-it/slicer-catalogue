import { TestBed } from '@angular/core/testing';

import { DescriptorsExpService } from './descriptors-exp.service';

describe('DescriptorsExpService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: DescriptorsExpService = TestBed.get(DescriptorsExpService);
    expect(service).toBeTruthy();
  });
});
