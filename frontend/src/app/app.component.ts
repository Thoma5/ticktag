import { Component, OnInit, ViewContainerRef } from '@angular/core';
import '../style/app.scss';
import { AuthService, User } from './service';
import { Router } from '@angular/router';
import { Overlay } from 'angular2-modal';
import { Modal } from 'angular2-modal/plugins/bootstrap';


@Component({
  selector: 'tt-app',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  private title: string;
  private user: User;
  private router: Router;

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(
    private authService: AuthService,
    private modal: Modal,
    overlay: Overlay, vcRef: ViewContainerRef) {
    this.title = 'TickTag';
    overlay.defaultViewContainer = vcRef;
  }


  ngOnInit(): void {
    this.user = this.authService.user;
    this.authService.observeUser()
      .subscribe(user => {
        console.log(user);
        this.user = user;
      });
  }

  logout(): void {
    this.authService.user = null;
    this.router.navigate(['/']);
  }
}
