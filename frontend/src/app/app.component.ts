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
  private user;

  constructor(private authService: AuthService) {
    this.title = 'TickTag';
  }

  ngOnInit(): void {
    this.user = this.authService.getUser();
    this.authService.observeUser()
      .subscribe(user => {
        console.log(user);
        this.user = user;
      });
  }

  logout(): void {
    this.authService.setUser(null);
  }
}
