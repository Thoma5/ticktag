import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs';
import {AuthApi, LoginResultJson, WhoamiResultJson} from '../api';
import {AuthService, ApiCallService} from '../service';

@Component({
  selector: 'tt-login',
  templateUrl: './login.component.html',
})
export class LoginComponent {
  private email: string;
  private password: string;

  constructor(private readonly router: Router,
              private readonly authApi: AuthApi,
              private readonly authService: AuthService,
              private readonly apiCallService: ApiCallService) {
  }

  onSubmit(): void {
    this.authService.user = null;
    let req = {
      email: this.email,
      password: this.password,
    };

    this.apiCallService
      .callNoError<LoginResultJson>(h => this.authApi.loginUsingPOSTWithHttpInfo(req, h))
      .flatMap(result => {
        if (result.token === '') {
          return Observable.of({token: '', authorities: new Array<string>()});
        } else {
          // Note that the AuthService as no user set yet.
          return this.apiCallService
            .callNoError<WhoamiResultJson>(h => this.authApi.whoamiUsingGETWithHttpInfo(h), {'X-Authorization': result.token})
            .map(x => ({token: result.token, authorities: x.authorities.slice()}));
        }
      })
      .subscribe(result => {
        if (result.token === '') {
          // TODO make this nice :)
          window.alert('Wrong username or password!');
        } else {
          this.authService.user = result;
          this.router.navigate(['/whoami']);
        }
      });
  }
}
