import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ApiCallService } from '../../service';
import {
  TicketApi, TicketResultJson, CommentsApi, AssignmenttagApi,
  AssignmentTagResultJson, CommentResultJson, TicketTagResultJson,
  TickettagApi, TimeCategoryJson, TimecategoryApi, UserResultJson,
  GetApi, GetResultJson, UpdateTicketRequestJson,
} from '../../api';
import { Observable } from 'rxjs';

class TicketDetailResult {
  constructor(
    public ticket: TicketResultJson,
    public comments: CommentResultJson[],
    public allAssignmentTags: AssignmentTagResultJson[],
    public allTicketTags: TicketTagResultJson[],
    public allTimeCategories: TimeCategoryJson[],
    public assignedUsers: UserResultJson[],
    public ticketCreator: UserResultJson,
  ) { }
}

@Component({
  selector: 'tt-ticket-detail',
  templateUrl: './ticket-detail.component.html',
  styleUrls: ['./ticket-detail.component.scss']
})
export class TicketDetailComponent implements OnInit {
  private loading = true;
  private ticketDetail: TicketDetailResult | null = null;

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private apiCallService: ApiCallService,
    private ticketApi: TicketApi,
    private commentsApi: CommentsApi,
    private assigmentTagsApi: AssignmenttagApi,
    private ticketTagsApi: TickettagApi,
    private timeCategoryApi: TimecategoryApi,
    private getApi: GetApi) {
  }

  ngOnInit(): void {
    this.route.params
      .do(() => { this.loading = true; })
      .switchMap(params => {
        let ticketId = '' + params['ticketNumber'];
        let projectId = '' + params['projectId'];

        return this.getTicketDetail(projectId, ticketId);
      })
      .subscribe(ticketDetail => {
        this.ticketDetail = ticketDetail;
        this.loading = false;
      });
  }

  onTitleChange(val: string): void {
    this.ticketDetail.ticket.title = val;
    let req = this.getEmptyUpdateRequest();
    req.title = val;
    this.updateTicket(req);
  }

  onDescriptionChange(val: string): void {
    this.ticketDetail.ticket.description = val;
    let req = this.getEmptyUpdateRequest();
    req.description = val;
    this.updateTicket(req);
  }

  onStorypointsChange(val: number): void {
    this.ticketDetail.ticket.storyPoints = val;
    let req = this.getEmptyUpdateRequest();
    req.storyPoints = val;
    this.updateTicket(req);
  }

  onTagAdd(val: string): void {
    // TODO endpoint missing...
    console.log('TODO add tag');
  }

  onTagRemove(val: string): void {
    // TODO endpoint missing...
    console.log('TODO remove tag');
  }

  private updateTicket(req: UpdateTicketRequestJson): void {
    let ticketId = this.ticketDetail.ticket.id;

    let updateObs = this.apiCallService
      .callNoError<TicketResultJson>(p => this.ticketApi.updateTicketUsingPUTWithHttpInfo(req, ticketId, p));
    updateObs.subscribe(ticket => {
      this.ticketDetail.ticket = ticket;
      console.log(ticket);
    });
  }

  private getEmptyUpdateRequest(): UpdateTicketRequestJson {
    return {
      title: null,
      open: null,
      storyPoints: null,
      currentEstimatedTime: null,
      dueDate: null,
      description: null,
      ticketAssignments: null,
      subTickets: null,
      existingSubTicketIds: null,
      partenTicketId: null,
    };
  }

  private getTicketDetail(projectId: string, ticketId: string): Observable<TicketDetailResult> {
    let ticketObs = this.apiCallService
      .callNoError<TicketResultJson>(p => this.ticketApi.getTicketUsingGETWithHttpInfo(ticketId, p));
    let commentsObs = this.apiCallService
      .callNoError<CommentResultJson[]>(p => this.commentsApi.listCommentsUsingGETWithHttpInfo(ticketId, p));
    let assignmentTagsObs = this.apiCallService
      .callNoError<AssignmentTagResultJson[]>(p => this.assigmentTagsApi.listAssignmentTagsUsingGETWithHttpInfo(projectId, p));
    let ticketTagsObs = this.apiCallService
      .callNoError<TicketTagResultJson[]>(p => this.ticketTagsApi.listTicketTagsUsingGETWithHttpInfo(null, projectId, p));
    let timeCategoriesObs = this.apiCallService
      .callNoError<TimeCategoryJson[]>(p => this.timeCategoryApi.listProjectTimeCategoriesUsingGETWithHttpInfo(projectId, p));

    // There is no dependency between these requests so we can execute them in paralell
    let zipped = Observable.zip(ticketObs, commentsObs, assignmentTagsObs, ticketTagsObs, timeCategoriesObs);

    return zipped.flatMap(tuple => {
      let ticketResult = tuple[0];
      // We need all assigned users
      let wantedUserIds = ticketResult.ticketAssignments.map(ta => ta.userId);
      // And the person who created it
      wantedUserIds.push(ticketResult.createdBy);

      let obs = this.apiCallService
        .callNoError<GetResultJson>(p => this.getApi.getUsingGETWithHttpInfo(wantedUserIds, p));

      // Transform the result into a nice dto
      return obs.map(getResult => {
        // filter(it => it) removes null/undefined
        let assignedUsers = ticketResult.ticketAssignments.map(ta => getResult.users[ta.userId]).filter(it => it);
        let creator = getResult.users[ticketResult.createdBy];

        return {
          ticket: tuple[0],
          comments: tuple[1],
          allAssignmentTags: tuple[2],
          allTicketTags: tuple[3],
          allTimeCategories: tuple[4],
          assignedUsers: assignedUsers,
          ticketCreator: creator,
        };
      });
    });
  }
}
