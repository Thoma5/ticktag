import { Component, OnInit, ViewContainerRef, OnDestroy, NgZone } from '@angular/core';
import '../style/app.scss';
import { AuthService, ApiCallService, User, ErrorHandler } from './service';
import { Router } from '@angular/router';
import { Overlay } from 'angular2-modal';
import { Modal } from 'angular2-modal/plugins/bootstrap';
import { Response } from '@angular/http';
import * as $ from 'jquery';


@Component({
  selector: 'tt-app',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy, ErrorHandler {
  private title: string;
  private user: User;
  private directTicketLinkEvent: (eventObject: JQueryEventObject) => any;

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(
    private authService: AuthService,
    private modal: Modal,
    private overlay: Overlay,
    private vcRef: ViewContainerRef,
    private router: Router,
    private zone: NgZone,
    private apiCallService: ApiCallService) {

    apiCallService.initErrorHandler(this);
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

    $(document).on('click', 'a.grammar-htmlifyCommands', this.directTicketLinkEvent = (e) => {
      e.preventDefault();
      this.zone.run(() => {
        this.router.navigate([
          '/project',
          e.currentTarget.getAttribute('data-projectId'),
          'ticket',
          e.currentTarget.getAttribute('data-ticketNumber'),
        ]);
      });
    });
  }

  ngOnDestroy() {
    if (this.directTicketLinkEvent) {
      $(document).off('click', 'a.grammar-htmlifyCommands', this.directTicketLinkEvent);
    }
  }

  onError(resp: any): void {
    if (resp instanceof Response) {

    } else {

    }
    console.dir(resp);
  }

  logout(): void {
    this.authService.user = null;
    this.router.navigate(['/']);
  }
}
