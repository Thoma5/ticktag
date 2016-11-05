import {Component, OnInit} from '@angular/core';
import {AuthApi, WhoamiResultJson} from '../api';
import {ApiCallService} from '../service';

@Component({
  selector: 'tt-whoami',
  templateUrl: './whoami.component.html',
})
export class WhoamiComponent implements OnInit {
  me: WhoamiResultJson;

  constructor(private readonly authApi: AuthApi,
              private readonly apiCallService: ApiCallService) {
  }

  ngOnInit(): void {
    this.getWhoami();
  }

  getWhoami(): void {
    this.apiCallService
      .callNoError<WhoamiResultJson>((h) => this.authApi.whoamiUsingGETWithHttpInfo(h))
      .subscribe(user => { this.me = user; });
  }
}
