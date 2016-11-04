import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {AuthApi} from '../api/api/AuthApi';
import {LoginRequestJson} from '../api/model/LoginRequestJson';
import {AuthService} from '../service/auth/auth.service';
import {User} from '../service/auth/user';
import {Headers} from '@angular/http';

@Component({
  selector: 'tt-login',
  templateUrl: './login.component.html',
})
export class LoginComponent {
  private email: string;
  private password: string;

  constructor(private router: Router,
              private authApi: AuthApi,
              private authService: AuthService) {
  }

  onSubmit(): void {
    let req: LoginRequestJson = {
      email: this.email,
      password: this.password,
    };

    // TODO api call
    this.authApi.loginUsingPOST(req)
      .flatMap(result => {
        let headers = new Headers();
        headers.append('X-Authorization', result.token);

        return this.authApi.whoamiUsingGET({'headers': headers})
          .map(whoami => {
            let u: User = {token: result.token, authorities: whoami.authorities};
            return u;
          });
      })
      .subscribe(result => {
        this.authService.setUser(result);
        this.router.navigate(['/whoami']);
      }, error => {
        alert(error);
      });
  }
}
