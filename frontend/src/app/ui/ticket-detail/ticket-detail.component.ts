import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ApiCallService } from '../../service';
import {
  TicketApi, TicketResultJson, CommentsApi, AssignmenttagApi,
  AssignmentTagResultJson, CommentResultJson, TicketTagResultJson,
} from '../../api';
import { Observable } from 'rxjs';
import {TicketEventResultJson} from '../../api/model/TicketEventResultJson';
import {TicketeventApi} from '../../api/api/TicketeventApi';

@Component({
  selector: 'tt-ticket-detail',
  templateUrl: './ticket-detail.component.html',
  styleUrls: ['./ticket-detail.component.scss']
})
export class TicketDetailComponent implements OnInit {
  private loading = true;
  private ticket: TicketResultJson | null = null;
  private comments: CommentResultJson[] | null = null;
  private assignmentTags: AssignmentTagResultJson[] | null = null;
  private ticketTags: TicketTagResultJson[] | null = null;
  private ticketEvents: TicketEventResultJson[] | null = null;

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private apiCallService: ApiCallService,
    private ticketApi: TicketApi,
    private commentsApi: CommentsApi,
    private assigmentTagsApi: AssignmenttagApi,
    private ticketEventApi: TicketeventApi,
    ) {
  }

  ngOnInit(): void {
    this.route.params
      .do(() => { this.loading = true; })
      .switchMap(params => {
        let ticketId = '' + params['ticketNumber'];
        let projectId = '' + params['projectId'];

        let ticketObs = this.apiCallService
          .callNoError<TicketResultJson>(p => this.ticketApi.getTicketUsingGETWithHttpInfo(ticketId, p));
        let commentsObs = this.apiCallService
          .callNoError<CommentResultJson[]>(p => this.commentsApi.listCommentsUsingGETWithHttpInfo(ticketId, p));
        let assignmentTagsObs = this.apiCallService
          .callNoError<AssignmentTagResultJson[]>(p => this.assigmentTagsApi.listAssignmentTagsUsingGETWithHttpInfo(projectId, p));
        let ticketEvents = this.apiCallService
          .callNoError<TicketEventResultJson[]>(p => this.ticketEventApi.listTicketEventsUsingGETWithHttpInfo(ticketId, p));

        let ticketTagsObs = Observable.of([]);

        return Observable.zip(ticketObs, commentsObs, assignmentTagsObs, ticketTagsObs, ticketEvents);
      })
      .subscribe(tuple => {
        let [ticket, comments, assignmentTags, ticketTags, ticketEvents] = tuple;
        this.ticket = ticket;
        // TODO remove this when we can actually use ticket tags
        this.ticket.tagIds = [];
        this.comments = comments;
        this.assignmentTags = assignmentTags;
        this.ticketTags = ticketTags;
        this.ticketEvents = ticketEvents;
        console.log(ticketEvents);
        console.log(comments);
        this.loading = false;
      });
  }
}
