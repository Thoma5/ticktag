import {User} from './user';
import {AuthService} from './auth.service';
import {LocalStorageService} from 'ng2-webstorage/dist/app';
import {TestBed, inject} from '@angular/core/testing';

class MockLocalStorageService extends LocalStorageService {
  private data = {};

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

  it('should return previously set value', inject([AuthService], (auth) => {
    let u: User = {
      token: 'token',
      authorities: ['USER']
    };
    auth.setUser(u);
    expect(auth.getUser()).toBe(u);
  }));

  it('should return null when no value was set', inject([AuthService], (auth) => {
    expect(auth.getUser()).toBe(null);
  }));
});
