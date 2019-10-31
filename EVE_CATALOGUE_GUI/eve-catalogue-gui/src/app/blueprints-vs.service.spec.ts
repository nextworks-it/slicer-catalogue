import { TestBed } from '@angular/core/testing';

import { BlueprintsVsService } from './blueprints-vs.service';

describe('BlueprintsVsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: BlueprintsVsService = TestBed.get(BlueprintsVsService);
    expect(service).toBeTruthy();
  });
});
