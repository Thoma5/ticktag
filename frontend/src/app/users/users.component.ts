import {Component, OnInit} from '@angular/core';
import {ApiCallService} from '../service';
import {UserApi, UserResultJson} from '../api';

@Component({
  selector: 'tt-users',
  templateUrl: './users.component.html',
})
export class UsersComponent implements OnInit {
  users: UserResultJson[];

  constructor(private readonly userApi: UserApi,
              private readonly apiCallService: ApiCallService) {
  }

  ngOnInit(): void {
    this.getUsers();
  }

  getUsers(): void {
    this.apiCallService
      .callNoError<UserResultJson[]>(h => this.userApi.listUsingGETWithHttpInfo(h))
      .subscribe(users => { this.users = users; });
  }
}
