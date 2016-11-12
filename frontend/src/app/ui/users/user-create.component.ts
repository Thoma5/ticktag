import {Component, Output, EventEmitter, OnInit} from '@angular/core';
import {ApiCallService} from '../../service';
import {UserApi, CreateUserRequestJson, UserResultJson} from '../../api';
import {RoleResultJson} from "../../api/model/RoleResultJson";
import RoleEnum = CreateUserRequestJson.RoleEnum;


@Component({
  selector: 'tt-user-create',
  templateUrl: './user-create.component.html',
})

export class UserCreateComponent implements OnInit{
  request: CreateUserRequestJson = {
    mail: '',
    name: '',
    password: '',
    role: RoleEnum.USER
  };
  working = false;
  private roles:RoleResultJson[] =[];

  ngOnInit(): void {
    this.getRoles();
  }

  getRoles(): void {
    this.apiCallService
      .callNoError<RoleResultJson[]>(h => this.userApi.rolesUsingGETWithHttpInfo(h))
      .subscribe(roles => { this.roles = roles;
      console.log(this.roles)});
  }

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
