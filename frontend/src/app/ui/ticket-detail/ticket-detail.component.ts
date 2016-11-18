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

export class Comment {
  constructor(
    public createTime: moment.Moment, public text: string,
    public name: string,
  ) { }
}


class MockTicketApi {
  private ticket: TicketResultJson;
  private comments: [Comment];

  constructor() {
    this.ticket = {
      number: 123,
      createTime: moment('1932-01-01 11:12:13'),
      title: 'Break the Enigma',
      /* tslint:disable */
      description: `
      The Enigma machines were a series of electro-mechanical rotor cipher machines developed and used in the early- to mid-twentieth century to protect commercial, diplomatic and military communication. Enigma was invented by the German engineer Arthur Scherbius at the end of World War I.[1] Early models were used commercially from the early 1920s, and adopted by military and government services of several countries, most notably Nazi Germany before and during World War II.[2] Several different Enigma models were produced, but the German military models are the most commonly recognised. However, Japanese and Italian models have been used
      `,
      /* tslint:enable */
      open: true,
      storyPoints: 8,
      initialEstimatedTime: moment.duration(4, 'months'),
      currentEstimatedTime: moment.duration(1, 'year'),
      dueDate: moment('1945-05-01 13:14:15'),
    };

    let c1 = {
      createTime: moment('1932-02-12 16:17:18'),
      text: 'Oh well, that wasn\'t so hard',
      name: 'Marian Rejewski',
    };
    let c2 = {
      createTime: moment('1940-01-17 19:20:21'),
      text: 'The new versions are more complex, maybe we can we automate it?',
      name: 'Alan Turing',
    };

    this.comments = [c1, c2];
  }

  getTicket(): Observable<TicketResultJson> {
    return Observable.of(this.ticket)
      .delay(500);
  }

  getComments(): Observable<Comment[]> {
    return Observable.of(this.comments)
      .delay(1000);
  }

  getTicketAndComments(): Observable<[TicketResultJson, Comment[]]> {
    return this.getTicket().zip(this.getComments());
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
  private comments: Comment[] | null;

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
        return this.ticketApi.getTicketAndComments();
      })
      .subscribe(
      result => {
        this.ticket = result[0];
        this.comments = result[1];
        this.loading = false;
      },
      () => { this.loading = false; });
  }
}
