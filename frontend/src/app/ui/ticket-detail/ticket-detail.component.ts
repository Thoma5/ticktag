import {AuthApi} from '../../api/api/AuthApi';
const uuidV4 = require('uuid/v4');
import { Component, OnInit, ViewContainerRef } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Modal } from 'angular2-modal/plugins/bootstrap';
import { Overlay } from 'angular2-modal';
import { ApiCallService, ApiCallResult } from '../../service';
import { TaskQueue } from '../../util/task-queue';
import {
  ProjectApi, ProjectResultJson, TicketApi, TicketResultJson, CommentsApi, AssignmenttagApi,
  AssignmentTagResultJson, CommentResultJson, TicketTagResultJson,
  TickettagApi, TimeCategoryJson, TimecategoryApi,
  GetApi, GetResultJson, UpdateTicketRequestJson,
  TicketuserrelationApi, TickettagrelationApi,
} from '../../api';
import { Observable, Subject } from 'rxjs';
import { TicketEventResultJson } from '../../api/model/TicketEventResultJson';
import { TicketeventApi } from '../../api/api/TicketeventApi';
import { LoggedtimeApi } from '../../api/api/LoggedtimeApi';
import {
  TicketDetail, TicketDetailTag, TicketDetailAssTag, TicketDetailComment,
  TicketDetailUser, TicketDetailTimeCategory, TicketDetailTransientUser,
  TicketDetailRelated, TicketDetailLoggedTime, TicketEvent, TicketEventParentChanged, TicketEventUserAdded,
  TicketEventUserRemoved, TicketEventLoggedTimeRemoved, TicketEventLoggedTimeAdded, TicketEventTagRemoved,
  TicketEventTagAdded, TicketDetailProgress,
  newTicketDetailRelated, newTransientTicketDetailRelated, TicketEventMentionAdded, TicketEventMentionRemoved
} from './ticket-detail';
import { SubticketCreateEvent } from './subticket-add/subticket-add.component';
import { idListToMap } from '../../util/listmaputils';
import * as imm from 'immutable';
import { CommandTextviewSaveEvent } from '../../util/command-textview/command-textview.component';
import { showValidationError } from '../../util/error';
import { Cmd } from '../../service/command/grammar';
import {WhoamiResultJson} from '../../api/model/WhoamiResultJson';
import {MemberApi} from '../../api/api/MemberApi';
import {MemberResultJson} from '../../api/model/MemberResultJson';
import ProjectRoleEnum = MemberResultJson.ProjectRoleEnum;

@Component({
  selector: 'tt-ticket-detail',
  templateUrl: './ticket-detail.component.html',
  styleUrls: ['./ticket-detail.component.scss']
})
export class TicketDetailComponent implements OnInit {
  private queue = new TaskQueue();

  // If you add something here make sure you also add it to refresh() and reset()
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
  private ticketTemplate: string;

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
  private transientSubtickets = imm.Map<string, TicketDetailRelated>();
  private transientTimes = imm.Map<string, boolean>();
  private userIsAllowedToEdit = true;

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(private route: ActivatedRoute,
    private router: Router,
    private apiCallService: ApiCallService,
    private ticketApi: TicketApi,
    private projectApi: ProjectApi,
    private commentsApi: CommentsApi,
    private assigmentTagsApi: AssignmenttagApi,
    private ticketTagsApi: TickettagApi,
    private timeCategoryApi: TimecategoryApi,
    private getApi: GetApi,
    private ticketEventApi: TicketeventApi,
    private ticketAssignmentApi: TicketuserrelationApi,
    private ticketTagRelationApi: TickettagrelationApi,
    private loggedTimeApi: LoggedtimeApi,
    private modal: Modal,
    private overlay: Overlay,
    private vcRef: ViewContainerRef,
    private authApi: AuthApi,
    private memberApi: MemberApi
  ) {
    overlay.defaultViewContainer = vcRef;
  }

  ngOnInit(): void {
    this.route.params
      .do(() => {
        this.loading = true;
      })
      .switchMap(params => {
        let ticketNumber = '' + params['ticketNumber'];
        let projectId = '' + params['projectId'];
        return this.reset()
          .flatMap(it => this.refresh(projectId, parseInt(ticketNumber, 10)));
      })
      .subscribe(() => {
        this.loading = false;
      });
  }

  onTitleChange(val: string): void {
    this.transientTicket.title = val;
    this.newTicketDetail();
    this.updateTicket({ title: { value: val } }, () => {
      this.transientTicket.title = undefined;
      this.newTicketDetail();
    });
  }

  onDescriptionChange(change: { text: string, commands: imm.List<Cmd> }): void {
    this.transientTicket.description = change.text;
    this.newTicketDetail();
    this.updateTicket({ description: { value: change.text }, commands: change.commands.toArray() }, () => {
      this.transientTicket.description = undefined;
      this.newTicketDetail();
    });
  }

  onStorypointsChange(val: number): void {
    this.transientTicket.storyPoints = val;
    this.newTicketDetail();
    this.updateTicket({ storyPoints: { value: val } }, () => {
      this.transientTicket.storyPoints = undefined;
      this.newTicketDetail();
    });
  }

  onCurrentEstimatedTimeChange(val: number) {
    this.transientTicket.currentEstimatedTime = val;
    this.newTicketDetail();
    this.updateTicket({ currentEstimatedTime: { value: val } }, () => {
      this.transientTicket.currentEstimatedTime = undefined;
      this.newTicketDetail();
    });
  }

  onInitialEstimatedTimeChange(val: number) {
    this.transientTicket.initialEstimatedTime = val;
    this.newTicketDetail();
    this.updateTicket({ initialEstimatedTime: { value: val } }, () => {
      this.transientTicket.initialEstimatedTime = undefined;
      this.newTicketDetail();
    });
  }

  onDueDateChange(val: number) {
    this.transientTicket.dueDate = val;
    this.newTicketDetail();
    this.updateTicket({ dueDate: { value: val } }, () => {
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
        .refresh(this.ticketDetail.projectId, this.ticketDetail.number)
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
        .refresh(this.ticketDetail.projectId, this.ticketDetail.number)
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
        .refresh(this.ticketDetail.projectId, this.ticketDetail.number)
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
        .refresh(this.ticketDetail.projectId, this.ticketDetail.number)
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

  onCommentCreate(event: CommandTextviewSaveEvent): void {
    this.creatingComment = true;
    let obs = this.apiCallService
      .call<void>(p => this.commentsApi.createCommentUsingPOSTWithHttpInfo({
        text: event.text,
        ticketId: this.ticketDetail.id,
        commands: event.commands.toArray(),
      }, p))
      .flatMap(result => this
        .refresh(this.ticketDetail.projectId, this.ticketDetail.number)
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
    let transientId = uuidV4();
    let transientTicket = newTransientTicketDetailRelated(val.title, val.description, false);
    this.transientSubtickets = this.transientSubtickets.set(transientId, transientTicket);
    this.newTicketDetail();

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
        .refresh(this.ticketDetail.projectId, this.ticketDetail.number)
        .map(() => result));
    this.queue.push(obs).subscribe(result => {
      if (!result.isValid) {
        // TODO nice message
        let ticketWithError = newTransientTicketDetailRelated(val.title, val.description, true);
        this.transientSubtickets = this.transientSubtickets.set(transientId, ticketWithError);
        this.error(result);
      } else {
        this.transientSubtickets = this.transientSubtickets.remove(transientId);
      }
      this.newTicketDetail();
    });
  }

  onUndoRedoTime(id: string, canceled: boolean) {
    this.transientTimes = this.transientTimes.set(id, canceled);
    let updateObs = this.apiCallService
      .callNoError<void>(p => this.loggedTimeApi.updateLoggedTimeUsingPUTWithHttpInfo({ canceled: canceled }, id, p))
      .flatMap(result => this
        .refresh(this.ticketDetail.projectId, this.ticketDetail.number)
        .map(() => result));
    this.queue.push(updateObs)
      .subscribe(() => {
        this.transientTimes = this.transientTimes.delete(id);
      });
  }

  scrollToCommentInput(event: KeyboardEvent) {
    if (event.altKey && event.key === 'c') {
      const element = document.querySelector('#comment-input');
      if (element) {
        element.scrollIntoView(element);
      }
    }
  }

  private updateTicket(req: UpdateTicketRequestJson, onFinish: () => void): void {
    let updateObs = this.apiCallService
      .call<void>(p => this.ticketApi.updateTicketUsingPUTWithHttpInfo(req, this.ticketDetail.id, p))
      .flatMap(result => this
        .refresh(this.ticketDetail.projectId, this.ticketDetail.number)
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
      this.transientSubtickets,
      this.relatedProgresses.get(this.currentTicketJson.id),
    );
  }

  private error(result: ApiCallResult<void | {}>): void {
    showValidationError(this.modal, result);
  }

  private reset(): Observable<void> {
    return Observable.defer(() => {
      this.ticketEvents = undefined;
      this.ticketDetail = undefined;
      this.allTicketTags = undefined;
      this.allAssignmentTags = undefined;
      this.allTimeCategories = undefined;
      this.interestingUsers = undefined;
      this.interestingLoggedTimes = undefined;
      this.comments = undefined;
      this.relatedTickets = undefined;
      this.relatedProgresses = undefined;
      this.ticketTemplate = undefined;

      this.currentTicketJson = undefined;
      this.transientUsers = imm.List<TicketDetailTransientUser>();
      this.transientTags = imm.Set<string>();
      this.transientTicket = undefined;

      this.currentTicketJson = undefined;
      this.transientUsers = imm.List<TicketDetailTransientUser>();
      this.transientTags = imm.Set<string>();
      this.transientTicket = {
        title: <string>undefined,
        storyPoints: <number>undefined,
        initialEstimatedTime: <number>undefined,
        currentEstimatedTime: <number>undefined,
        dueDate: <number>undefined,
        description: <string>undefined,
      };
      this.creatingComment = false;
      this.commentResetEventObservable = new Subject<string>();
      return Observable.of(undefined);
    });
  }

  private refresh(projectId: string, ticketNumber: number): Observable<void> {
    this.apiCallService
      .callNoError<WhoamiResultJson>((h) => this.authApi.whoamiUsingGETWithHttpInfo(h))
      .subscribe(me => {
        if (me.authorities.includes('ADMIN')) {
          this.userIsAllowedToEdit = true;
        } else {

          this.apiCallService
            .call<MemberResultJson>((h) => this.memberApi.getMemberUsingGETWithHttpInfo(me.id, projectId, h))
            .subscribe(result => {
              if (result.isValid) {
                if (result.result.projectRole === ProjectRoleEnum.ADMIN || result.result.projectRole === ProjectRoleEnum.USER) {
                  this.userIsAllowedToEdit = true;
                } else {
                  this.userIsAllowedToEdit = false;
                }
              } else {
                this.userIsAllowedToEdit = false;
              }
            });
        }

      });

    let transientUsers = this.transientUsers;  // Backup to avoid data races

    let rawTicketObs = this.apiCallService
      .callNoError<TicketResultJson>(p => this.ticketApi.getTicketByNumberUsingGETWithHttpInfo(projectId, ticketNumber, p));

    let projectObs = this.apiCallService.callNoError<ProjectResultJson>(p => this.projectApi.getProjectUsingGETWithHttpInfo(projectId, p));

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

    let rawCommentObs = rawTicketObs.flatMap(rawTicket => this.apiCallService
      .callNoError<CommentResultJson[]>(p => this.commentsApi.listCommentsUsingGETWithHttpInfo(rawTicket.id, p)));

    let rawTicketEventObs = rawTicketObs.flatMap(rawTicket => this.apiCallService
      .callNoError<TicketEventResultJson[]>(p => this.ticketEventApi.listTicketEventsUsingGETWithHttpInfo(rawTicket.id, p)));

    let getObs = Observable.zip(rawTicketObs, rawCommentObs, rawTicketEventObs).flatMap(tuple => {
      let [ticket, comments, events] = tuple;

      // We need all assigned users
      let wantedUserIds = ticket.ticketUserRelations.map(ta => ta.userId);
      // And the comment authors
      comments.forEach(c => {
        wantedUserIds.push(c.userId);
      });
      // And transient users
      transientUsers.forEach(tu => {
        wantedUserIds.push(tu.user.id);
      });
      // And the person who created it
      wantedUserIds.push(ticket.createdBy);

      // We need the subtickets
      let wantedTicketIds = ticket.subTicketIds.slice();
      // And referenced tickets
      wantedTicketIds.push(...ticket.referencedTicketIds);
      // And tickets that reference this ticket
      wantedTicketIds.push(...ticket.referencingTicketIds);
      // And our parent
      if (ticket.parentTicketId != null) {
        wantedTicketIds.push(ticket.parentTicketId);
      }

      // We need users, comments and parent tickets from events
      events.forEach(event => {
        let e: any = event;
        wantedUserIds.push(e.userId);
        switch (e.type) {
          case 'TicketEventParentChangedResultJson':
            if (e.srcParentId) { wantedTicketIds.push(e.srcParentId); }
            if (e.dstParentId) { wantedTicketIds.push(e.dstParentId); }
            break;
          case 'TicketEventUserAddedResultJson':
            wantedUserIds.push(e.addedUserId);
            break;
          case 'TicketEventUserRemovedResultJson':
            wantedUserIds.push(e.removedUserId);
            break;
          case 'TicketEventMentionAddedResultJson':
            wantedTicketIds.push(e.mentionedTicketId);
            break;
          case 'TicketEventMentionRemovedResultJson':
            wantedTicketIds.push(e.mentionedTicketId);
            break;
        }
      });

      // Tickets needed for progress
      let wantedStatisticIds = new Array<string>();
      // The ticket itself
      wantedStatisticIds.push(ticket.id);
      // And all subtickets
      wantedStatisticIds.push(...ticket.subTicketIds);

      let wantedLoggedTimeIds = new Array<string>().concat(...comments.map(c => c.loggedTimeIds));

      return this.apiCallService
        .callNoError<GetResultJson>(p => this.getApi.getUsingPOSTWithHttpInfo({
          userIds: wantedUserIds,
          ticketIds: wantedTicketIds,
          ticketIdsForStatistic: wantedStatisticIds,
          loggedTimeIds: wantedLoggedTimeIds,
        }, p));
    });

    return Observable.zip(rawTicketObs, assignmentTagsObs, ticketTagsObs, timeCategoriesObs, rawCommentObs,
      Observable.zip(rawTicketEventObs, getObs, projectObs))
      .do(tuple => {
        let [ticket, assignmentTags, ticketTags, timeCategories, comments, [ticketEvents, get, project]] = tuple;

        this.allTicketTags = ticketTags;
        this.allAssignmentTags = assignmentTags;
        this.allTimeCategories = timeCategories;
        this.currentTicketJson = ticket;
        this.ticketTemplate = project.ticketTemplate;
        this.relatedProgresses = imm.Map(get.ticketStatistics).map((p, tid) => new TicketDetailProgress(tid, p)).toMap();
        this.interestingUsers = imm.Map(get.users).map(u => new TicketDetailUser(u)).toMap();
        this.relatedTickets = imm.Map(get.tickets).map(t => newTicketDetailRelated(t, this.relatedProgresses)).toMap();
        this.interestingLoggedTimes = imm.Map(get.loggedTimes)
          .map(lt => new TicketDetailLoggedTime(lt, this.allTimeCategories))
          .toMap();
        this.comments = idListToMap(comments)
          .map(c => new TicketDetailComment(c, this.interestingUsers, this.interestingLoggedTimes))
          .toMap();
        this.ticketEvents = imm.List(ticketEvents)
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
              case 'TicketEventMentionAddedResultJson':
                return new TicketEventMentionAdded(e, this.interestingUsers, this.relatedTickets);
              case 'TicketEventMentionRemovedResultJson':
                return new TicketEventMentionRemoved(e, this.interestingUsers, this.relatedTickets);
              default:
                return new TicketEvent(e, this.interestingUsers);
            }
          }).toList();
        this.newTicketDetail();
      })
      .map(it => undefined)
      .catch(err => Observable.empty<void>());
  }
}
