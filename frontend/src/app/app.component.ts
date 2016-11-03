import {Component, OnInit} from '@angular/core';
import '../style/app.scss';
import {AuthService} from './service/auth/auth.service';

@Component({
  selector: 'tt-app',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  private title;
  private loggedIn;

  constructor(private authService: AuthService) {
    this.title = 'TickTag';
  }

  ngOnInit(): void {
    this.loggedIn = this.authService.getToken() != null;
    this.authService.observeToken()
      .subscribe(token => {
        console.log(token);
        this.loggedIn = token != null;
      });
  }

  logout(): void {
    this.authService.setToken(null);
  }
}
