import { TestBed } from '@angular/core/testing';

import { BlueprintsEcService } from './blueprints-ec.service';

describe('BlueprintsEcService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: BlueprintsEcService = TestBed.get(BlueprintsEcService);
    expect(service).toBeTruthy();
  });
});
