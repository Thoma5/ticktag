import {Component, OnInit} from '@angular/core';
import '../style/app.scss';
import {AuthService, User} from './service';

@Component({
  selector: 'tt-app',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  private title: string;
  private user: User;

  constructor(private readonly authService: AuthService) {
    this.title = 'TickTag';
  }

  ngOnInit(): void {
    this.user = this.authService.user;
    this.authService.observeUser()
      .subscribe(user => {
        console.log(user);
        this.user = user;
      });
  }

  logout(): void {
    this.authService.user = null;
  }
}
