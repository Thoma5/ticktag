import { Component, Output, EventEmitter, OnInit } from '@angular/core';
import { ApiCallService } from '../../../service';
import { UserApi, CreateUserRequestJson, UserResultJson } from '../../../api';
import { RoleResultJson } from '../../../api/model/RoleResultJson';
import RoleEnum = CreateUserRequestJson.RoleEnum;


@Component({
  selector: 'tt-user-create',
  templateUrl: './user-create.component.html',
  styleUrls: ['./user-create.component.scss']
})

export class UserCreateComponent implements OnInit {
  readonly MAIL_REGEX = '^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$';
  readonly PASSWD_REGEX = '^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}';
  readonly USERNAME_REGEX = '^[a-z0-9_]{3,30}$';

  request: CreateUserRequestJson = {
    username: '',
    mail: '',
    name: '',
    password: '',
    role: RoleEnum.USER,
    image: '',
  };
  working = false;
  confPassword: string = undefined;
  imageWithMimeType: String = undefined;
  private roles: RoleResultJson[] = [];
  @Output() readonly created = new EventEmitter<UserResultJson>();

  ngOnInit(): void {
    this.getRoles();
  }

  getRoles(): void {
    this.apiCallService
      .callNoError<RoleResultJson[]>(h => this.userApi.listRolesUsingGETWithHttpInfo(h))
      .subscribe(roles => {
        this.roles = roles;
      });
  }

  constructor(private apiCallService: ApiCallService,
    private userApi: UserApi) { }

  onSubmit(): void {
    this.working = true;
    this.apiCallService
      .call<UserResultJson>(h => this.userApi.createUserUsingPOSTWithHttpInfo(this.request, h))
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

  setImage(img: string) {
    this.imageWithMimeType = img;
    this.request.image = img ? img.split(',')[1] : undefined;
  }
}
