import {Component, OnInit} from '@angular/core';
import {AuthApi} from '../api/api/AuthApi';
import {WhoamiResultJson} from '../api/model/WhoamiResultJson';
import {Headers} from '@angular/http';
import {AuthService} from '../service/auth/auth.service';

@Component({
  selector: 'tt-whoami',
  templateUrl: './whoami.component.html',
})
export class WhoamiComponent implements OnInit {
  private me: WhoamiResultJson;

  constructor(private authApi: AuthApi,
              private authService: AuthService) {
  }

  ngOnInit(): void {
    this.getWhoami();
  }

  getWhoami(): void {
    // TODO api call
    let headers = new Headers();
    let token = this.authService.getUser().token;
    headers.append('X-Authorization', token);

    this.authApi.whoamiUsingGET({'headers': headers})
      .subscribe(res => this.me = res, err => alert(err));
  }
}
