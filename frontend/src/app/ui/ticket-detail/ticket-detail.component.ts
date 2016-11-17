import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
// import { Response, ResponseContentType } from '@angular/http';
import { ApiCallService } from '../../service';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/switchMap';


class TicketResultJson {
  constructor(public title: string, public description: string) {
  }
}

class MockTicketApi {
  private ticket: TicketResultJson;

  constructor() {
    this.ticket = new TicketResultJson('Implement ticket details UI', 'It is severly lacking at the moment');
  }

  getTicket(): Observable<TicketResultJson> {
    return Observable.of(this.ticket)
      .delay(150);
  }
}


@Component({
  selector: 'tt-ticket-detail',
  templateUrl: './ticket-detail.component.html',
})
export class TicketDetailComponent implements OnInit {
  private ticketApi: MockTicketApi;
  private loading = false;
  private ticket: TicketResultJson | null;

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private apiCallService: ApiCallService) {
    this.ticketApi = new MockTicketApi();
  }

  ngOnInit(): void {
    this.route.params
      .switchMap((params: Params) => {
        // let projectId = +params['projectId'];
        // let ticketNumber = +params['ticketNumber'];

        this.loading = true;
        return this.ticketApi.getTicket();
      })
      .subscribe(
      result => {
        this.ticket = result;
        this.loading = false;
      },
      () => { this.loading = false; });
  }
}
