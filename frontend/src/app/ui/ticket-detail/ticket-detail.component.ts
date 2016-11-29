import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Modal } from 'angular2-modal/plugins/bootstrap';
import { ApiCallService, ApiCallResult } from '../../service';
import { TaskQueue } from '../../util/task-queue';
import {
  TicketApi, TicketResultJson, CommentsApi, AssignmenttagApi,
  AssignmentTagResultJson, CommentResultJson, TicketTagResultJson,
  TickettagApi, TimeCategoryJson, TimecategoryApi,
  GetApi, GetResultJson, UpdateTicketRequestJson,
  TicketuserrelationApi, TickettagrelationApi,
} from '../../api';
import { Observable, Subject } from 'rxjs';
import { TicketEventResultJson } from '../../api/model/TicketEventResultJson';
import { TicketeventApi } from '../../api/api/TicketeventApi';
import {
  TicketDetail, TicketDetailTag, TicketDetailAssTag, TicketDetailComment,
  TicketDetailUser, TicketDetailTimeCategory, TicketDetailTransientUser,
  TicketDetailRelated, TicketDetailLoggedTime, TicketEvent, TicketEventParentChanged, TicketEventUserAdded,
  TicketEventUserRemoved, TicketEventLoggedTimeRemoved, TicketEventLoggedTimeAdded, TicketEventTagRemoved,
  TicketEventTagAdded, TicketDetailProgress
} from './ticket-detail';
import { SubticketCreateEvent } from './subticket-add/subticket-add.component';
import { idListToMap } from '../../util/listmaputils';
import * as imm from 'immutable';
import { CommentTextviewSaveEvent } from './command-textview/command-textview.component';

@Component({
  selector: 'tt-ticket-detail',
  templateUrl: './ticket-detail.component.html',
  styleUrls: ['./ticket-detail.component.scss']
})
export class TicketDetailComponent implements OnInit {
  private queue = new TaskQueue();

  private loading = true;
  private ticketEvents: imm.List<TicketEvent>;
  private ticketDetail: TicketDetail = null;
  private allTicketTags: imm.Map<string, TicketDetailTag>;
  private allAssignmentTags: imm.Map<string, TicketDetailAssTag>;
  private allTimeCategories: imm.Map<string, TicketDetailTimeCategory>;
  private interestingUsers: imm.Map<string, TicketDetailUser>;
  private interestingLoggedTimes: imm.Map<string, TicketDetailLoggedTime>;
  private comments: imm.Map<string, TicketDetailComment>;
  private relatedTickets: imm.Map<string, TicketDetailRelated>;
  private relatedProgresses: imm.Map<string, TicketDetailProgress>;

  // Internal state
  private currentTicketJson: TicketResultJson;  // Only use to recreate the ticket
  private transientUsers = imm.List<TicketDetailTransientUser>();
  private transientTags = imm.Set<string>();
  private transientTicket = {
    title: <string>undefined,
    storyPoints: <number>undefined,
    initialEstimatedTime: <number>undefined,
    currentEstimatedTime: <number>undefined,
    dueDate: <number>undefined,
    description: <string>undefined,
  };
  private creatingComment = false;
  private commentResetEventObservable = new Subject<string>();

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(private route: ActivatedRoute,
    private router: Router,
    private apiCallService: ApiCallService,
    private ticketApi: TicketApi,
    private commentsApi: CommentsApi,
    private assigmentTagsApi: AssignmenttagApi,
    private ticketTagsApi: TickettagApi,
    private timeCategoryApi: TimecategoryApi,
    private getApi: GetApi,
    private ticketEventApi: TicketeventApi,
    private ticketAssignmentApi: TicketuserrelationApi,
    private ticketTagRelationApi: TickettagrelationApi,
    private modal: Modal) {
  }

  ngOnInit(): void {
    this.route.params
      .do(() => {
        this.loading = true;
      })
      .switchMap(params => {
        let ticketId = '' + params['ticketNumber'];
        let projectId = '' + params['projectId'];
        return this.refresh(projectId, ticketId);
      })
      .subscribe(() => {
        this.loading = false;
      });
  }

  onTitleChange(val: string): void {
    this.transientTicket.title = val;
    this.newTicketDetail();
    this.updateTicket({ title: val }, () => {
      this.transientTicket.title = undefined;
      this.newTicketDetail();
    });
  }

  onDescriptionChange(val: string): void {
    this.transientTicket.description = val;
    this.newTicketDetail();
    this.updateTicket({ description: val }, () => {
      this.transientTicket.description = undefined;
      this.newTicketDetail();
    });
  }

  onStorypointsChange(val: number): void {
    this.transientTicket.storyPoints = val;
    this.newTicketDetail();
    this.updateTicket({ storyPoints: val }, () => {
      this.transientTicket.storyPoints = undefined;
      this.newTicketDetail();
    });
  }

  onCurrentEstimatedTimeChange(val: number) {
    this.transientTicket.currentEstimatedTime = val;
    this.newTicketDetail();
    this.updateTicket({ currentEstimatedTime: val }, () => {
      this.transientTicket.currentEstimatedTime = undefined;
      this.newTicketDetail();
    });
  }

  onInitialEstimatedTimeChange(val: number) {
    this.transientTicket.initialEstimatedTime = val;
    this.newTicketDetail();
    this.updateTicket({ initialEstimatedTime: val }, () => {
      this.transientTicket.initialEstimatedTime = undefined;
      this.newTicketDetail();
    });
  }

  onDueDateChange(val: number) {
    this.transientTicket.dueDate = val;
    this.newTicketDetail();
    this.updateTicket({ dueDate: val }, () => {
      this.transientTicket.dueDate = undefined;
      this.newTicketDetail();
    });
  }

  onTagAdd(tagId: string): void {
    this.transientTags = this.transientTags.add(tagId);
    this.newTicketDetail();
    let obs = this.apiCallService
      .call<void>(p => this.ticketTagRelationApi.setTicketTagRelationUsingPUTWithHttpInfo(this.ticketDetail.id, tagId, p))
      .flatMap(result => this
        .refresh(this.ticketDetail.projectId, this.ticketDetail.id)
        .map(() => result));
    this.queue.push(obs).subscribe(result => {
      this.transientTags = this.transientTags.remove(tagId);
      this.newTicketDetail();
      if (!result.isValid) {
        // TODO nice message
        this.error(result);
      }
    });
  }

  onTagRemove(tagId: string): void {
    this.transientTags = this.transientTags.add(tagId);
    this.newTicketDetail();
    let obs = this.apiCallService
      .call<void>(p => this.ticketTagRelationApi.deleteTicketTagRelationUsingDELETEWithHttpInfo(this.ticketDetail.id, tagId, p))
      .flatMap(result => this
        .refresh(this.ticketDetail.projectId, this.ticketDetail.id)
        .map(() => result));
    this.queue.push(obs).subscribe(result => {
      this.transientTags = this.transientTags.remove(tagId);
      this.newTicketDetail();
      if (!result.isValid) {
        // TODO nice message
        this.error(result);
      }
    });
  }

  onAssignmentAdd(ass: { user: string, tag: string }) {
    let user = this.interestingUsers.get(ass.user);
    this.addTransientTagNoUpdate(user, ass.tag);
    this.newTicketDetail();
    let obs = this.apiCallService
      .call<void>(p => this.ticketAssignmentApi.createTicketAssignmentUsingPOSTWithHttpInfo(
        this.ticketDetail.id,
        ass.tag,
        ass.user,
        p))
      .flatMap(result => this
        .refresh(this.ticketDetail.projectId, this.ticketDetail.id)
        .map(() => result));
    this.queue.push(obs).subscribe(result => {
      this.removeTransientTagNoUpdate(user, ass.tag);
      this.newTicketDetail();
      if (!result.isValid) {
        // TODO nice message
        this.error(result);
      }
    });
  }

  onAssignmentRemove(ass: { user: string, tag: string }) {
    let userBackup = this.interestingUsers.get(ass.user);
    this.addTransientTagNoUpdate(userBackup, ass.tag);
    this.newTicketDetail();
    let obs = this.apiCallService
      .call<void>(p => this.ticketAssignmentApi.deleteTicketAssignmentUsingDELETEWithHttpInfo(
        this.ticketDetail.id,
        ass.tag,
        ass.user,
        p))
      .flatMap(result => this
        .refresh(this.ticketDetail.projectId, this.ticketDetail.id)
        .map(() => result));
    this.queue.push(obs)
      .subscribe(result => {
        this.removeTransientTagNoUpdate(userBackup, ass.tag);
        this.addTransientUserNoUpdate(userBackup);
        this.newTicketDetail();
        if (!result.isValid) {
          // TODO nice message
          this.error(result);
        }
      });
  }

  onAssignmentUserAdd(user: TicketDetailUser) {
    this.addTransientUserNoUpdate(user);
    this.newTicketDetail();
  }

  onCommentCreate(event: CommentTextviewSaveEvent): void {
    this.creatingComment = true;
    let obs = this.apiCallService
      .call<void>(p => this.commentsApi.createCommentUsingPOSTWithHttpInfo({
        text: event.text,
        ticketId: this.ticketDetail.id,
        commands: event.commands.toArray(),
      }, p))
      .flatMap(result => this
        .refresh(this.ticketDetail.projectId, this.ticketDetail.id)
        .map(() => result));
    this.queue.push(obs).subscribe(result => {
      this.creatingComment = false;
      if (!result.isValid) {
        // TODO nice message
        this.error(result);
      } else {
        this.commentResetEventObservable.next('');
      }
    });
  }

  onSubticketAdd(val: SubticketCreateEvent): void {
    // TODO define default values
    let obs = this.apiCallService
      .call(p => this.ticketApi.createTicketUsingPOSTWithHttpInfo({
        title: val.title,
        open: true,
        storyPoints: null,
        initialEstimatedTime: null,
        currentEstimatedTime: null,
        dueDate: this.ticketDetail.dueDate,
        projectId: val.projectId,
        description: val.description,
        ticketAssignments: [],
        subTickets: [],
        existingSubTicketIds: [],
        parentTicketId: val.parentTicketId,
        commands: val.commands.toArray(),
      }, p))
      .flatMap(result => this
        .refresh(this.ticketDetail.projectId, this.ticketDetail.id)
        .map(() => result));
    this.queue.push(obs).subscribe(result => {
      if (!result.isValid) {
        // TODO nice message
        this.error(result);
      }
    });
  }

  private updateTicket(req: UpdateTicketRequestJson, onFinish: () => void): void {
    let updateObs = this.apiCallService
      .call<void>(p => this.ticketApi.updateTicketUsingPUTWithHttpInfo(req, this.ticketDetail.id, p))
      .flatMap(result => this
        .refresh(this.ticketDetail.projectId, this.ticketDetail.id)
        .map(() => result));
    this.queue.push(updateObs)
      .subscribe((result) => {
        onFinish();
        if (!result.isValid) {
          // TODO nice message
          this.error(result);
        }
      });
  }

  private addTransientUserNoUpdate(user: TicketDetailUser) {
    let knownUser = this.interestingUsers.get(user.id);
    if (!knownUser) {
      this.interestingUsers = this.interestingUsers.set(user.id, user);
      knownUser = user;
    }

    if (!this.transientUsers.find(v => v.user.id === knownUser.id)) {
      this.transientUsers = this.transientUsers.push(new TicketDetailTransientUser(knownUser, imm.Set<string>()));
    }
  }

  private addTransientTagNoUpdate(user: TicketDetailUser, tagId: string) {
    this.addTransientUserNoUpdate(user);
    let i = this.transientUsers.findIndex(v => v.user.id === user.id);
    let old = this.transientUsers.get(i);
    if (!old.tags.contains(tagId)) {
      this.transientUsers = this.transientUsers.set(i, new TicketDetailTransientUser(
        old.user,
        old.tags.add(tagId),
      ));
    }
  }

  private removeTransientTagNoUpdate(user: TicketDetailUser, tagId: string) {
    let i = this.transientUsers.findIndex(v => v.user.id === user.id);
    if (i >= 0) {
      let old = this.transientUsers.get(i);
      if (old.tags.contains(tagId)) {
        this.transientUsers = this.transientUsers.set(i, new TicketDetailTransientUser(
          old.user,
          old.tags.remove(tagId)
        ));
      }
    }
  }

  private newTicketDetail() {
    this.ticketDetail = new TicketDetail(
      this.currentTicketJson,
      this.comments,
      this.relatedTickets,
      this.interestingUsers,
      this.allTicketTags,
      this.allAssignmentTags,
      this.relatedProgresses,
      this.transientUsers,
      this.transientTags,
      this.transientTicket,
      this.relatedProgresses.get(this.currentTicketJson.id),
      );
  }

  private error(result: ApiCallResult<void|{}>): void {
    console.dir(result);
    let validationErrors = result.error;

    let errorBody = '<ul>';
    validationErrors.map(e => {
      let baseStr = e.field + ': ';
      let errorStr = 'unknown';
      if (e.type === 'size') {
        errorStr = 'size (' + e.sizeInfo.min + ', ' + e.sizeInfo.max + ')';
      } else if (e.type === 'pattern') {
        errorStr = 'pattern ' + e.patternInfo.pattern;
      } else if (e.type === 'other') {
        errorStr = 'other ' + e.otherInfo.name;
      }
      return baseStr + errorStr;
    }).forEach(s => {
      errorBody = errorBody + '<li>' + s + '</li>';
    });
    errorBody = errorBody + '</ul>';

    this.modal.alert()
        .size('sm')
        .showClose(true)
        .title('Error')
        .body(errorBody)
        .open();
  }

  private refresh(projectId: string, ticketId: string): Observable<void> {
    let transientUsers = this.transientUsers;  // Backup to avoid data races

    let rawTicketObs = this.apiCallService
      .callNoError<TicketResultJson>(p => this.ticketApi.getTicketUsingGETWithHttpInfo(ticketId, p));
    let rawCommentsObs = this.apiCallService
      .callNoError<CommentResultJson[]>(p => this.commentsApi.listCommentsUsingGETWithHttpInfo(ticketId, p));
    let rawTicketEventsObs = this.apiCallService
      .callNoError<TicketEventResultJson[]>(p => this.ticketEventApi.listTicketEventsUsingGETWithHttpInfo(ticketId, p));
    let assignmentTagsObs = this.apiCallService
      .callNoError<AssignmentTagResultJson[]>(p => this.assigmentTagsApi.listAssignmentTagsUsingGETWithHttpInfo(projectId, p))
      .map(ats => imm.List(ats)
        .sortBy(at => at.name.toLocaleLowerCase())
        .map((at, i) => new TicketDetailAssTag(at, i))
        .groupBy(at => at.id)
        .map(at => at.get(0))
        .toMap());
    let ticketTagsObs = this.apiCallService
      .callNoError<TicketTagResultJson[]>(p => this.ticketTagsApi.listTicketTagsUsingGETWithHttpInfo(null, projectId, p))
      .map(tts => idListToMap(tts).map(tt => new TicketDetailTag(tt)).toMap());
    let timeCategoriesObs = this.apiCallService
      .callNoError<TimeCategoryJson[]>(p => this.timeCategoryApi.listProjectTimeCategoriesUsingGETWithHttpInfo(projectId, p))
      .map(tcs => idListToMap(tcs).map(tc => new TicketDetailTimeCategory(tc)).toMap());

    // There is no dependency between these requests so we can execute them in parallel
    return Observable
      .zip(rawTicketObs, rawCommentsObs, assignmentTagsObs, ticketTagsObs, timeCategoriesObs, rawTicketEventsObs)
      .flatMap(tuple => {
        let ticketResult = tuple[0];

        // We need all assigned users
        let wantedUserIds = ticketResult.ticketUserRelations.map(ta => ta.userId);
        // And the comment authors
        tuple[1].forEach(c => {
          wantedUserIds.push(c.userId);
        });
        // And transient users
        transientUsers.forEach(tu => {
          wantedUserIds.push(tu.user.id);
        });
        // And the person who created it
        wantedUserIds.push(ticketResult.createdBy);

        // We need the subtickets
        let wantedTicketIds = ticketResult.subTicketIds.slice();
        // And referenced tickets
        wantedTicketIds.push(...ticketResult.referencedTicketIds);
        // And tickets that reference this ticket
        wantedTicketIds.push(...ticketResult.referencingTicketIds);

        // We need users, comments and parent tickets from events
        let eventResult = tuple[5];
        eventResult.forEach(event => {
          let e: any = event;
          wantedUserIds.push(e.userId);
          switch (e.type) {
            case 'TicketEventParentChangedResultJson':
              if (e.srcParentId) { wantedTicketIds.push(e.srcParentId); }
              if (e.dstParentId) { wantedTicketIds.push(e.dstParentId); }
              break;
          }
        });

        // Tickets needed for progress
        let wantedStatisticIds = new Array<string>();
        // The ticket itself
        wantedStatisticIds.push(ticketId);
        // And all subtickets
        wantedStatisticIds.push(...ticketResult.subTicketIds);

        let getObs = this.apiCallService
          .callNoError<GetResultJson>(p => this.getApi.getUsingPOSTWithHttpInfo({
            userIds: wantedUserIds,
            ticketIds: wantedTicketIds,
            ticketIdsForStatistic: wantedStatisticIds,
          }, p));
        return Observable.zip(Observable.of(tuple), getObs);
      })
      .flatMap(tuple => {
        let wantedLoggedTimeIds = new Array<string>().concat(...tuple[0][1].map(c => c.loggedTimeIds));
        let getObs = this.apiCallService
          .callNoError<GetResultJson>(p => this.getApi.getUsingPOSTWithHttpInfo({ loggedTimeIds: wantedLoggedTimeIds }, p));
          return Observable.zip(Observable.of(tuple[0]), Observable.of(tuple[1]), getObs);
      })
      .do(tuple => {
        this.allTicketTags = tuple[0][3];
        this.allAssignmentTags = tuple[0][2];
        this.allTimeCategories = tuple[0][4];
        this.currentTicketJson = tuple[0][0];
        this.relatedProgresses = imm.Map(tuple[1].ticketStatistics).map((p, tid) => new TicketDetailProgress(tid, p)).toMap();
        this.interestingUsers = imm.Map(tuple[1].users).map(u => new TicketDetailUser(u)).toMap();
        this.relatedTickets = imm.Map(tuple[1].tickets).map(t => new TicketDetailRelated(t, this.relatedProgresses)).toMap();
        this.interestingLoggedTimes = imm.Map(tuple[2].loggedTimes)
          .map(lt => new TicketDetailLoggedTime(lt, this.allTimeCategories))
          .toMap();
        this.comments = idListToMap(tuple[0][1])
          .map(c => new TicketDetailComment(c, this.interestingUsers, this.interestingLoggedTimes))
          .toMap();
        this.ticketEvents = imm.List(tuple[0][5])
          .map(event => {
            let e: any = event;
            switch (e.type) {
              case 'TicketEventParentChangedResultJson':
                return new TicketEventParentChanged(e, this.interestingUsers, this.relatedTickets);
              case 'TicketEventUserRemovedResultJson':
                return new TicketEventUserRemoved(e, this.interestingUsers, this.allAssignmentTags);
              case 'TicketEventUserAddedResultJson':
                return new TicketEventUserAdded(e, this.interestingUsers, this.allAssignmentTags);
              case 'TicketEventLoggedTimeRemovedResultJson':
                return new TicketEventLoggedTimeRemoved(e, this.interestingUsers, this.allTimeCategories);
              case 'TicketEventLoggedTimeAddedResultJson':
                return new TicketEventLoggedTimeAdded(e, this.interestingUsers, this.allTimeCategories);
              case 'TicketEventTagRemovedResultJson':
                return new TicketEventTagRemoved(e, this.interestingUsers, this.allTicketTags);
              case 'TicketEventTagAddedResultJson':
                return new TicketEventTagAdded(e, this.interestingUsers, this.allTicketTags);
              default:
                return new TicketEvent(e, this.interestingUsers);
            }
          }).toList();
        this.newTicketDetail();
      })
      .map(it => undefined);
  }
}
