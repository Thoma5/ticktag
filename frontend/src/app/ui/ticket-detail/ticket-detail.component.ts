import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { Response, ResponseContentType } from '@angular/http';
import { ApiCallService } from '../../service';
import {
  TicketApi, TicketResultJson, CommentsApi, AssignmenttagApi, AssignmentTagResultJson,
  CommentResultJson
} from '../../api';
import { Observable } from 'rxjs';

@Component({
  selector: 'tt-ticket-detail',
  templateUrl: './ticket-detail.component.html',
  styleUrls: ['./ticket-detail.component.scss']
})
export class TicketDetailComponent implements OnInit {
  private loading = true;
  private ticket: TicketResultJson | null;
  private comments: CommentResultJson[] | null;
  private assignmentTags: AssignmentTagResultJson[] | null;

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private apiCallService: ApiCallService,
    private ticketApi: TicketApi,
    private commentsApi: CommentsApi,
    private assigmentTagsApi: AssignmenttagApi) {
  }

  ngOnInit(): void {
    this.route.params
      .do(() => { this.loading = true; })
      .flatMap(params => {
        let ticketId = '' + params['ticketNumber'];
        let projectId = '' + params['projectId'];

        let ticketObs = this.apiCallService
          .callNoError<TicketResultJson>(p => this.ticketApi.getTicketUsingGETWithHttpInfo(ticketId, p));
        let commentsObs = this.apiCallService
          .callNoError<CommentResultJson[]>(p => this.ticketApi.listCommentsUsingGET1WithHttpInfo(ticketId, p));
        let assignmentTagsObs = this.apiCallService
          .callNoError<AssignmentTagResultJson[]>(p => this.assigmentTagsApi.listAssignmentTagsUsingGETWithHttpInfo(projectId, p));

        return Observable.zip(ticketObs, commentsObs, assignmentTagsObs);
      })
      .subscribe(tuple => {
        let [ticket, comments, assignmentTags] = tuple;
        this.ticket = ticket;
        this.comments = comments;
        this.assignmentTags = assignmentTags;
        this.loading = false;
      });
  }
}
