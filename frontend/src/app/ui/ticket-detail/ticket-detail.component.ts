import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ApiCallService } from '../../service';
import { TaskQueue } from '../../util/task-queue';
import {
  TicketApi, TicketResultJson, CommentsApi, AssignmenttagApi,
  AssignmentTagResultJson, CommentResultJson, TicketTagResultJson,
  TickettagApi, TimeCategoryJson, TimecategoryApi,
  GetApi, GetResultJson, UpdateTicketRequestJson, TicketAssignmentJson,
  TicketassignmentApi
} from '../../api';
import { Observable } from 'rxjs';
import {
  TicketDetail, TicketDetailTag, TicketDetailAssTag, TicketDetailComment,
  TicketDetailUser, TicketDetailTimeCategory
} from './ticket-detail';
import { idListToMap } from '../../util/listmaputils';
import * as imm from 'immutable';

@Component({
  selector: 'tt-ticket-detail',
  templateUrl: './ticket-detail.component.html',
  styleUrls: ['./ticket-detail.component.scss']
})
export class TicketDetailComponent implements OnInit {
  private queue = new TaskQueue();

  private loading = true;
  private ticketDetail: TicketDetail = null;
  private allTicketTags: imm.Map<string, TicketDetailTag>;
  private allAssignmentTags: imm.Map<string, TicketDetailAssTag>;
  private allTimeCategories: imm.Map<string, TicketDetailTimeCategory>;

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
    private getApi: GetApi,
    private ticketAssignmentApi: TicketassignmentApi) {
  }

  ngOnInit(): void {
    this.route.params
      .do(() => { this.loading = true; })
      .flatMap(params => {
        let ticketId = '' + params['ticketNumber'];
        let projectId = '' + params['projectId'];
        return this.refresh(projectId, ticketId);
      })
      .subscribe(() => { this.loading = false; });
  }

  onTitleChange(val: string): void {
    this.updateTicket({ title: val });
  }

  onDescriptionChange(val: string): void {
    this.updateTicket({ description: val });
  }

  onStorypointsChange(val: number): void {
    this.updateTicket({ storyPoints: val });
  }

  onTagAdd(val: string): void {
    // TODO endpoint missing...
    console.log('TODO add tag');
  }

  onTagRemove(val: string): void {
    console.log('TODO remove tag');
  }

  onAssignmentAdd(ass: TicketAssignmentJson) {
    let obs = this.apiCallService
      .call<void>(p => this.ticketAssignmentApi.createTicketAssignmentUsingPOSTWithHttpInfo(
        this.ticketDetail.id,
        ass.assignmentTagId,
        ass.userId,
        p));
    this.queue.push(obs)
      .subscribe(result => {
        if (result.isValid) {
          // TODO is this clever?
          this.refresh(this.ticketDetail.projectId, this.ticketDetail.id).subscribe();
        } else {
          // TODO nice message
          console.dir(result);
          window.alert('😥');
        }
      });
  }

  onAssignmentRemove(ass: TicketAssignmentJson) {
    let obs = this.apiCallService
      .call<void>(p => this.ticketAssignmentApi.deleteTicketAssignmentUsingDELETEWithHttpInfo(
        this.ticketDetail.id,
        ass.assignmentTagId,
        ass.userId,
        p));
    this.queue.push(obs)
      .subscribe(result => {
        if (result.isValid) {
          // TODO is this clever?
          this.refresh(this.ticketDetail.projectId, this.ticketDetail.id).subscribe();
        } else {
          // TODO nice message
          console.dir(result);
          window.alert('😥');
        }
      });
  }

  private updateTicket(req: UpdateTicketRequestJson): void {
    // TODO: callNoError is wrong here
    let updateObs = this.apiCallService
      .call<void>(p => this.ticketApi.updateTicketUsingPUTWithHttpInfo(req, this.ticketDetail.id, p));
    this.queue.push(updateObs)
      .subscribe((result) => {
        if (result.isValid) {
          // TODO is this clever?
          this.refresh(this.ticketDetail.projectId, this.ticketDetail.id).subscribe();
        } else {
          // TODO nice message
          console.dir(result);
          window.alert('😥');
        }
      });
  }

  private refresh(projectId: string, ticketId: string): Observable<void> {
    let rawTicketObs = this.apiCallService
      .callNoError<TicketResultJson>(p => this.ticketApi.getTicketUsingGETWithHttpInfo(ticketId, p));
    let rawCommentsObs = this.apiCallService
      .callNoError<CommentResultJson[]>(p => this.commentsApi.listCommentsUsingGETWithHttpInfo(ticketId, p));
    let assignmentTagsObs = this.apiCallService
      .callNoError<AssignmentTagResultJson[]>(p => this.assigmentTagsApi.listAssignmentTagsUsingGETWithHttpInfo(projectId, p))
      .map(ats => idListToMap(ats).map(at => new TicketDetailAssTag(at, 0)).toMap());  // TODO ordering
    let ticketTagsObs = this.apiCallService
      .callNoError<TicketTagResultJson[]>(p => this.ticketTagsApi.listTicketTagsUsingGETWithHttpInfo(null, projectId, p))
      .map(tts => idListToMap(tts).map(tt => new TicketDetailTag(tt)).toMap());
    let timeCategoriesObs = this.apiCallService
      .callNoError<TimeCategoryJson[]>(p => this.timeCategoryApi.listProjectTimeCategoriesUsingGETWithHttpInfo(projectId, p))
      .map(tcs => idListToMap(tcs).map(tc => new TicketDetailTimeCategory(tc)).toMap());

    // There is no dependency between these requests so we can execute them in paralell
    return Observable
      .zip(rawTicketObs, rawCommentsObs, assignmentTagsObs, ticketTagsObs, timeCategoriesObs)
      .flatMap(tuple => {
        let ticketResult = tuple[0];
        // We need all assigned users
        let wantedUserIds = ticketResult.ticketAssignments.map(ta => ta.userId);
        // And the comment authors
        tuple[1].forEach(c => { wantedUserIds.push(c.userId); });
        // And the person who created it
        wantedUserIds.push(ticketResult.createdBy);

        let getObs = this.apiCallService
          .callNoError<GetResultJson>(p => this.getApi.getUsingGETWithHttpInfo(wantedUserIds, p));

        return Observable.zip(Observable.of(tuple), getObs);
      })
      .do(tuple => {
        let users = imm.Map(tuple[1].users).map(u => new TicketDetailUser(u)).toMap();
        let comments = idListToMap(tuple[0][1]).map(c => new TicketDetailComment(c, users)).toMap();
        this.allTicketTags = tuple[0][3];
        this.allAssignmentTags = tuple[0][2];
        this.allTimeCategories = tuple[0][4];
        this.ticketDetail = new TicketDetail(tuple[0][0], comments, users, tuple[0][3], tuple[0][2]);
      })
      .map(it => undefined);
  }
}
