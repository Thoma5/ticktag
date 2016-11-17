import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
// import { Response, ResponseContentType } from '@angular/http';
import { ApiCallService } from '../../service';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/switchMap';
import {Duration, Instant} from 'js-joda';


class TicketResultJson {
  constructor(
    public title: string, public description: string, public open: boolean,
    public storyPoints: number, public initialEstimatedTime: Duration,
    public dueDate: Instant,
  ) {}
}

class MockTicketApi {
  private ticket: TicketResultJson;

  constructor() {
    this.ticket = {
      title: 'Implement ticket details UI',
      description: 'It is severly lacking at the moment',
      open: false,
      storyPoints: 8,
      initialEstimatedTime: Duration.ofHours(2),
      dueDate: Instant.ofEpochMilli(0),
    };
  }

  getTicket(): Observable<TicketResultJson> {
    return Observable.of(this.ticket)
      .delay(150);
  }
}


@Component({
  selector: 'tt-ticket-detail',
  templateUrl: './ticket-detail.component.html',
  styleUrls: ['./ticket-detail.component.scss']
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
