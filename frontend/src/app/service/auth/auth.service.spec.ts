import {User} from './user';
import {AuthService} from './auth.service';
import {LocalStorageService} from 'ng2-webstorage/dist/app';
import {TestBed, inject} from '@angular/core/testing';

class MockLocalStorageService extends LocalStorageService {
  private data: {[key: string]: any} = {};

  store(key: string, value: any): void {
    this.data[key] = value;
  }

  retrieve(key: string): any {
    return this.data[key];
  }
}

describe('Auth Service', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AuthService, {
        provide: LocalStorageService,
        useClass: MockLocalStorageService
      }]
    });
  });

  it('should return previously set value', inject([AuthService], (auth: AuthService) => {
    let u: User = {
      id: 'id',
      token: 'token',
      authorities: ['USER']
    };
    auth.user = u;
    expect(auth.user).toBe(u);
  }));

  it('should return null when no value was set', inject([AuthService], (auth: AuthService) => {
    expect(auth.user).toBe(null);
  }));
});
