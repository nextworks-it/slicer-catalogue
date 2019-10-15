import { TestBed } from '@angular/core/testing';

import { VsbGraphService } from './vsb-graph.service';

describe('VsbGraphService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: VsbGraphService = TestBed.get(VsbGraphService);
    expect(service).toBeTruthy();
  });
});
