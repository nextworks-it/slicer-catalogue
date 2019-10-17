import { TestBed } from '@angular/core/testing';

import { BlueprintsEcServiceService } from './blueprints-ec-service.service';

describe('BlueprintsEcServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: BlueprintsEcServiceService = TestBed.get(BlueprintsEcServiceService);
    expect(service).toBeTruthy();
  });
});
