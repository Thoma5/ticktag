import {Component, OnInit} from '@angular/core';
import {Headers} from '@angular/http';
import {AuthService} from '../service/auth/auth.service';
import {UserResultJson} from '../api/model/UserResultJson';
import {UserApi} from '../api/api/UserApi';

@Component({
  selector: 'tt-users',
  templateUrl: './users.component.html',
})
export class UsersComponent implements OnInit {
  private users: UserResultJson[];

  constructor(private userApi: UserApi,
              private authService: AuthService) {
  }

  ngOnInit(): void {
    this.getUsers();
  }

  getUsers(): void {
    // TODO api call
    let headers = new Headers();
    let token = this.authService.getUser().token;
    headers.append('X-Authorization', token);

    this.userApi.listUsingGET({'headers': headers})
      .subscribe(res => this.users = res, err => alert(err));
  }
}
