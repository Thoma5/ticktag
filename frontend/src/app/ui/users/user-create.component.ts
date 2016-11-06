import {Component, Output, EventEmitter} from '@angular/core';
import {ApiCallService} from '../../service';
import {UserApi, CreateUserRequestJson, UserResultJson} from '../../api';

@Component({
  selector: 'tt-user-create',
  templateUrl: './user-create.component.html',
})
export class UserCreateComponent {
  request: CreateUserRequestJson = {
    mail: '',
    name: '',
    password: '',
  };
  working = false;
  @Output() readonly created = new EventEmitter<UserResultJson>();

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(private apiCallService: ApiCallService,
              private userApi: UserApi) {}

  onSubmit(): void {
    this.working = true;
    this.apiCallService
      .call<UserResultJson>(h => this.userApi.createUsingPOSTWithHttpInfo(this.request, h))
      .subscribe(
        result => {
          if (result.isValid) {
            this.request.mail = '';
            this.request.name = '';
            this.request.password = '';
            this.created.emit(result.result);
          } else {
            window.alert('Could not create user:\n\n' + JSON.stringify(result.error));
          }
        },
        undefined,
        () => { this.working = false; });
  }
}
