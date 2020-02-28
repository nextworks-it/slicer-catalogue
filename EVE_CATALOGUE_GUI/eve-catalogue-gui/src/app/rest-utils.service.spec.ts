import { TestBed } from '@angular/core/testing';

import { RestUtilsService } from './rest-utils.service';

describe('RestUtilsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: RestUtilsService = TestBed.get(RestUtilsService);
    expect(service).toBeTruthy();
  });
});
