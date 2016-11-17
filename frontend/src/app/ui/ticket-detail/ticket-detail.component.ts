import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
// import { Response, ResponseContentType } from '@angular/http';
import { ApiCallService } from '../../service';
import { Observable } from 'rxjs';
import * as moment from 'moment';


export class TicketResultJson {
  constructor(
    public number: number, public createTime: moment.Moment, public title: string,
    public description: string, public open: boolean, public storyPoints: number,
    public initialEstimatedTime: moment.Duration, public currentEstimatedTime: moment.Duration,
    public dueDate: moment.Moment,
  ) {}
}

class MockTicketApi {
  private ticket: TicketResultJson;

  constructor() {
    this.ticket = {
      number: 123,
      createTime: moment.utc(0),
      title: 'Implement ticket details UI',
      description: 'It is severly lacking at the moment',
      open: false,
      storyPoints: 8,
      initialEstimatedTime: moment.duration(2, 'hours'),
      currentEstimatedTime: moment.duration(3, 'hours'),
      dueDate: moment.utc(60 * 60 * 2),
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
