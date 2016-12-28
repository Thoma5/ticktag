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
  request: CreateUserRequestJson = {
    username: '',
    mail: '',
    name: '',
    password: '',
    role: RoleEnum.USER,
    image: '',
  };
  working = false;
  pwdConfFailed = false;
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
        console.log(this.roles);
      });
  }

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(private apiCallService: ApiCallService,
    private userApi: UserApi) { }

  onSubmit(): void {
    if (this.request.password === this.confPassword) {
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
    } else {
      this.pwdConfFailed = true;
    }
  }
    setImage(img: string) {
    this.imageWithMimeType = img;
    this.request.image = img ? img.split(',')[1] : undefined;
  }
}