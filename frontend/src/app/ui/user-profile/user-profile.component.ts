import { Component, Output, EventEmitter, OnInit, ViewContainerRef } from '@angular/core';
import { ApiCallService, AuthService, ApiCallResult } from '../../service';
import { AuthApi, UserApi, UpdateUserRequestJson, UserResultJson, WhoamiResultJson, LoginResultJson } from '../../api';
import { showValidationError } from '../../util/error';
import { Modal } from 'angular2-modal/plugins/bootstrap';
import { Overlay } from 'angular2-modal';
import { Observable } from 'rxjs';


@Component({
  selector: 'tt-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})

export class UserProfileComponent implements OnInit {
  loading = true;
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
  private user: UserResultJson;
  @Output() readonly updated = new EventEmitter<UserResultJson>();

  constructor(private apiCallService: ApiCallService,
    private userApi: UserApi,
    private authApi: AuthApi,
    private authService: AuthService,
    private modal: Modal,
    private overlay: Overlay,
    private vcRef: ViewContainerRef,
  ) {
    overlay.defaultViewContainer = vcRef;
  }

  ngOnInit(): void {
    this.apiCallService
      .callNoError<WhoamiResultJson>((h) => this.authApi.whoamiUsingGETWithHttpInfo(h))
      .subscribe(me => {
        this.apiCallService
          .callNoError<UserResultJson>((h) => this.userApi.getUserUsingGETWithHttpInfo(me.id, h))
          .subscribe(user => {
            this.user = user;
            this.defaultImage = (<any>this.userApi).basePath + '/user/image/' + this.user.imageId;
            this.revert();
            this.loading = false;
          });
      });

  }

  onSubmit(): void {
    if (this.request.password && !this.request.oldPassword) {
      this.modal.alert()
        .size('sm')
        .showClose(true)
        .title('Error')
        .body('Please enter your old password')
        .open();
      return;
    }
    if (this.request.password && this.request.password.length < 8) {
      this.modal.alert()
        .size('sm')
        .showClose(true)
        .title('Error')
        .body('Password Length must at least be 8 characters')
        .open();
      return;
    }

    this.working = true;

    this.apiCallService
      .call<UserResultJson>(h => this.userApi.updateUserUsingPUTWithHttpInfo(this.user.id, this.request, h))
      .subscribe(
      result => {
        if (result.isValid) {
          this.updated.emit(result.result);
          this.login();
          this.revert();
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

  private login() {
    this.authService.user = null;
    let req = {
      email: this.request.mail,
      password: this.request.password,
    };

    this.apiCallService
      .callNoError<LoginResultJson>(h => this.authApi.loginUsingPOSTWithHttpInfo(req, h))
      .flatMap(result => {
        if (result.token === '') {
          return Observable.of(null);
        } else {
          // Note that the AuthService as no user set yet.
          return this.apiCallService
            .callNoError<WhoamiResultJson>(h => this.authApi.whoamiUsingGETWithHttpInfo(h), { 'X-Authorization': result.token })
            .map(x => {
              return {
                id: x.id,
                token: result.token,
                authorities: x.authorities.slice()
              };
            });
        }
      })
      .subscribe(result => {
        if (result !== null) {
          this.authService.user = result;
        }
      });
  }
}
