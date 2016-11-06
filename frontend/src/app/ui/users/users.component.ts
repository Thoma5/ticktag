import {Component, OnInit} from '@angular/core';
import {ApiCallService} from '../../service';
import {UserApi, UserResultJson} from '../../api';

@Component({
  selector: 'tt-users',
  templateUrl: './users.component.html',
})
export class UsersComponent implements OnInit {
  users: UserResultJson[];

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(private userApi: UserApi,
              private apiCallService: ApiCallService) {
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
