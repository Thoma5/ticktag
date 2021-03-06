import { Component, Input, Output, EventEmitter, OnInit, ViewContainerRef } from '@angular/core';
import { ApiCallService, ApiCallResult } from '../../../service';
import { UserApi, UpdateUserRequestJson, UserResultJson, WhoamiResultJson } from '../../../api';
import { RoleResultJson } from '../../../api/model/RoleResultJson';
import { showValidationError } from '../../../util/error';
import { Modal } from 'angular2-modal/plugins/bootstrap';
import { Overlay } from 'angular2-modal';


@Component({
  selector: 'tt-user-update',
  templateUrl: './user-update.component.html',
  styleUrls: ['./user-update.component.scss']
})

export class UserUpdateComponent implements OnInit {
  readonly MAIL_REGEX = '^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$';
  readonly USERNAME_REGEX = '^[a-z0-9_]{3,30}$';

  request: UpdateUserRequestJson = {
    mail: undefined,
    name: undefined,
    password: undefined,
    role: undefined,
    image: undefined,
    disabled: undefined,
  };
  active: Boolean;
  username: string = '';
  working = false;
  confPassword: string = undefined;
  storedImageLink: string;
  private defaultImage: string = undefined;
  private roles: RoleResultJson[] = [];
  @Input() user: UserResultJson;
  @Input() currentUser: WhoamiResultJson;
  @Output() readonly updated = new EventEmitter<UserResultJson>();

  constructor(private apiCallService: ApiCallService,
    private userApi: UserApi,
    private modal: Modal,
    private overlay: Overlay,
    private vcRef: ViewContainerRef,
  ) {
    overlay.defaultViewContainer = vcRef;
  }

  ngOnInit(): void {
    this.defaultImage = (<any>this.userApi).basePath + '/user/image/' + this.user.imageId;
    this.getRoles();
    this.revert();
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
      .call<UserResultJson>(h => this.userApi.updateUserUsingPUTWithHttpInfo(this.user.id, this.request, h))
      .subscribe(
      result => {
        if (result.isValid) {
          this.request.mail = '';
          this.request.name = '';
          this.request.password = '';
          this.updated.emit(result.result);
        } else {
          this.error(result);
        }
      },
      undefined,
      () => { this.working = false; });
  }
  setImage(img: string) {
    this.storedImageLink = img;
    if (img && img !== this.defaultImage) {
      this.request.image = img.split(',')[1];
    } else if (img && img === this.defaultImage) {
      this.request.image = undefined;
    } else {
      this.request.image = '';
    }
  }

  revert() {
    this.request.mail = this.user.mail;
    this.request.name = this.user.name;
    this.username = this.user.username;
    this.request.role = this.user.role;
    this.request.disabled = this.user.disabled;
    this.request.password = undefined;
    this.request.oldPassword = undefined;
    this.confPassword = undefined;
    this.request.image = undefined;
    this.active = !this.user.disabled;
    this.storedImageLink = this.defaultImage;
  }

  changeActive() {
    this.request.disabled = !this.active;
  }

  private error(result: ApiCallResult<void | {}>): void {
    showValidationError(this.modal, result);
  }
}
