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
  ) { }
}

class MockTicketApi {
  private ticket: TicketResultJson;

  constructor() {
    this.ticket = {
      number: 123,
      createTime: moment.utc(0),
      title: 'Implement ticket details UI',
      /* tslint:disable */
      description:
      `Leverage agile frameworks to provide a robust synopsis for high level overviews. Iterative approaches to corporate strategy foster collaborative thinking to further the overall value proposition. Organically grow the holistic world view of disruptive innovation via workplace diversity and empowerment.
      Bring to the table win-win survival strategies to ensure proactive domination. At the end of the day, going forward, a new normal that has evolved from generation X is on the runway heading towards a streamlined cloud solution. User generated content in real-time will have multiple touchpoints for offshoring.
      Capitalise on low hanging fruit to identify a ballpark value added activity to beta test. Override the digital divide with additional clickthroughs from DevOps. Nanotechnology immersion along the information highway will close the loop on focusing solely on the bottom line.
      `,
      /* tslint:enable */
      open: false,
      storyPoints: 8,
      initialEstimatedTime: moment.duration(2, 'hours'),
      currentEstimatedTime: moment.duration(3, 'hours'),
      dueDate: moment.utc(60 * 60 * 2),
    };
  }

  getTicket(): Observable<TicketResultJson> {
    return Observable.of(this.ticket)
      .delay(500);
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
