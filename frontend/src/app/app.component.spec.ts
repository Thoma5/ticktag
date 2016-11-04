import {TestBed} from '@angular/core/testing';
import {provideRoutes} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';
import {AppComponent} from './app.component';
import {AuthService} from './service/auth/auth.service';
import {User} from './service/auth/user';
import {Observable} from 'rxjs/Rx';
import {Injectable} from '@angular/core';

@Injectable()
class MockAuthService extends AuthService {
  getUser(): User|null {
    return null;
  }

  setUser(user: User|null): void {
  }

  observeUser(): Observable<User|null> {
    return Observable.empty<User|null>();
  }
}

describe('App', () => {
  // provide our implementations or mocks to the dependency injector
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [AppComponent],
      providers: [provideRoutes([]), {provide: AuthService, useClass: MockAuthService}],
    });
  });

  it('should have a title', () => {
    let fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    expect(fixture.debugElement.componentInstance.title).toEqual('TickTag');
  });
});
