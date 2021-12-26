import { TestBed } from '@angular/core/testing';

import { AuthonticateService } from './authonticate.service';

describe('AuthonticateService', () => {
  let service: AuthonticateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthonticateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
