import { TestBed } from '@angular/core/testing';

import { AuthService } from 'src/app/services/auth.service';

describe('RegisterService', () => {
  let service: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
