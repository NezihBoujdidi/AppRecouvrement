import { TestBed } from '@angular/core/testing';

import { PaiementSpecService } from './paiement-spec.service';

describe('PaiementSpecService', () => {
  let service: PaiementSpecService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PaiementSpecService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
