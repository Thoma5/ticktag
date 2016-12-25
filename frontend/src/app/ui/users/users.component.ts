import {Component, OnInit} from '@angular/core';
import {ApiCallService} from '../../service';
import {UserApi, PageUserResultJson, UserResultJson} from '../../api';

@Component({
  selector: 'tt-users',
  templateUrl: './users.component.html',
})
export class UsersComponent implements OnInit {
  users: UserResultJson[];
  disabled = false;

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(private userApi: UserApi,
              private apiCallService: ApiCallService) {
  }

  ngOnInit(): void {
    this.getUsers();
  }

  getUsers(): void {
    this.apiCallService
      .callNoError<PageUserResultJson>(h => this.userApi
      .listUsersUsingGETWithHttpInfo(0, 50, undefined, undefined, undefined, this.disabled, h))
      .subscribe(users => { this.users = users.content; });
  }
}
