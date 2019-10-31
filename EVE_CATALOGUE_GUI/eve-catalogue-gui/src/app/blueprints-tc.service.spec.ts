import { TestBed } from '@angular/core/testing';

import { BlueprintsTcService } from './blueprints-tc.service';

describe('BlueprintsTcService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: BlueprintsTcService = TestBed.get(BlueprintsTcService);
    expect(service).toBeTruthy();
  });
});
