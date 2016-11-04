import {Injectable} from '@angular/core';
import {LocalStorageService} from 'ng2-webstorage';
import {Observable} from 'rxjs';
import {User} from './user';

const KEY_USER = 'user';

@Injectable()
export class AuthService {
  constructor(private readonly localStorageService: LocalStorageService) {
  }

  get user(): User|null {
    let stored = this.localStorageService.retrieve(KEY_USER);
    if (stored) {
      return stored;
    } else {
      return null;
    }
  }

  set user(user: User|null) {
    this.localStorageService.store(KEY_USER, user);
  }

  observeUser(): Observable<User|null> {
    return this.localStorageService.observe(KEY_USER).asObservable();
  }
}
