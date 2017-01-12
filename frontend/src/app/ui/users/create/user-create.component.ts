import { Component, Output, EventEmitter, OnInit } from '@angular/core';
import { ApiCallService, ApiCallResult } from '../../../service';
import { UserApi, CreateUserRequestJson, UserResultJson } from '../../../api';
import { RoleResultJson } from '../../../api/model/RoleResultJson';
import RoleEnum = CreateUserRequestJson.RoleEnum;
import { showValidationError } from '../../../util/error';
import { Modal } from 'angular2-modal/plugins/bootstrap';


@Component({
  selector: 'tt-user-create',
  templateUrl: './user-create.component.html',
  styleUrls: ['./user-create.component.scss']
})

export class UserCreateComponent implements OnInit {
  readonly MAIL_REGEX = '^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$';
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

  constructor(private apiCallService: ApiCallService,
    private userApi: UserApi,
    private modal: Modal) { }

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
          this.error(result);
        }
      },
      undefined,
      () => { this.working = false; });
  }

  setImage(img: string) {
    this.imageWithMimeType = img;
    this.request.image = img ? img.split(',')[1] : undefined;
  }

  private error(result: ApiCallResult<void | {}>): void {
    showValidationError(this.modal, result);
  }
}
