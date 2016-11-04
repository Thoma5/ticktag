import {Injectable} from '@angular/core';
import {LocalStorageService} from 'ng2-webstorage';
import {Observable} from 'rxjs/Rx';
import {User} from './user';

const KEY_USER = 'user';

@Injectable()
export class AuthService {
  constructor(private localStorageService: LocalStorageService) {
  }

  getUser(): User|null {
    let stored = this.localStorageService.retrieve(KEY_USER);
    if (stored) {
      return stored;
    } else {
      return null;
    }
  }

  setUser(user: User|null): void {
    this.localStorageService.store(KEY_USER, user);
  }

  observeUser(): Observable<User|null> {
    return this.localStorageService.observe(KEY_USER).asObservable();
  }
}
