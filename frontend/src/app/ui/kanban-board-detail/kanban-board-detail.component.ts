import {Component, OnInit} from '@angular/core';
import * as imm from 'immutable';
import {ApiCallService} from '../../service/api-call/api-call.service';
import {BoardApi} from '../../api/api/BoardApi';
import {Router, ActivatedRoute} from '@angular/router';
import {Observable, Subject} from 'rxjs';
import {UserResultJson} from '../../api/model/UserResultJson';
import {Tag} from '../../util/taginput/taginput.component';
import {TicketTagResultJson} from '../../api/model/TicketTagResultJson';
import {TicketResultJson} from '../../api/model/TicketResultJson';
import {KanbanColumnResultJson} from '../../api/model/KanbanColumnResultJson';
import {GetResultJson} from '../../api/model/GetResultJson';
import {GetApi} from '../../api/api/GetApi';
import {TickettagApi} from '../../api/api/TickettagApi';
import {idListToMap} from '../../util/listmaputils';
import {KanbanBoard} from '../kanban-boards/kanban-boards.component';
import {
  TicketDetailProgress, TicketDetailUser, TicketDetailRelated,
  newTicketDetailRelated
} from '../ticket-detail/ticket-detail';
import {DragulaService} from 'ng2-dragula';
import {UpdateKanbanColumnJson} from '../../api/model/UpdateKanbanColumnJson';
import {TaskQueue} from '../../util/task-queue';
import {TickettagrelationApi} from '../../api/api/TickettagrelationApi';
import {TicketOverviewTag, TicketOverviewUser} from '../ticket-overview/ticket-overview';
import {ProjectApi} from '../../api/api/ProjectApi';
import {TicketFilter} from '../ticket-overview/ticket-filter/ticket-filter';
import {CollectEvent, FindSubTicketEvent} from './kanban-cell/kanban-cell.component';
import {KanbanBoardResultJson} from '../../api/model/KanbanBoardResultJson';
import {Location} from '@angular/common';
@Component({
  selector: 'tt-kanban-board-detail',
  templateUrl: './kanban-board-detail.component.html',
  styleUrls: ['./kanban-board-detail.component.scss']
})
export class KanbanBoardDetailComponent implements OnInit {
  private queue = new TaskQueue();

  private kanbanBoard: KanbanBoard;
  private kanbanColumns: imm.List<KanbanDetailColumn>;
  private allTicketTags: imm.Map<string, KanbanDetailTag>;
  private interestingTickets: imm.Map<string, KanbanDetailTicket>;
  private interestingUsers: imm.Map<string, TicketDetailUser>;
  private relatedProgresses: imm.Map<string, TicketDetailProgress>;
  private allTicketTagsForFilter: imm.Map<string, TicketOverviewTag>;
  private allProjectUsers: imm.Map<string, TicketOverviewUser>;
  private ticketFilter: TicketFilter = new TicketFilter(undefined, undefined, undefined, undefined, undefined,
    undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined);
  private loading = true;
  private filterTerms = new Subject<TicketFilter>();
  query: string;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private apiCallService: ApiCallService,
              private getApi: GetApi,
              private ticketTagsApi: TickettagApi,
              private kanbanBoardApi: BoardApi,
              private dragulaService: DragulaService,
              private projectApi: ProjectApi,
              private ticketTagRelationApi: TickettagrelationApi,
              private location: Location) {
    dragulaService.dropModel.subscribe((value: any) => {
      this.onDropModel(value.slice(1));
    });
  }

  shouldBeHidden(ticket: KanbanDetailTicket): boolean {
    return ticket.number % 2 === 0;
  }

  private onDropModel(args: any) {
    let target: HTMLDivElement = args[1];
    let el: HTMLDivElement = args[0];
    this.updateModel(el.getAttribute('id'), target.getAttribute('id'));
  }

  onCollet(collectEvent: CollectEvent) {
    let obs = this.apiCallService
      .callNoError<void>(p => this.kanbanBoardApi.collectSubTicketsUsingPUTWithHttpInfo(collectEvent.ticketId, collectEvent.tagId, p));
    obs.subscribe(result => {
        this.refresh(this.kanbanBoard.projectId, this.kanbanBoard.id).subscribe();
      }
    );
  }

  private updateModel(ticketId: string, targetTagId: string) {
    let column: UpdateKanbanColumnJson;
    this.kanbanColumns.forEach(c => {
      if (c.id === targetTagId) {
        let newArray: string[] = [];
        for (let t of c.tickets) {
          newArray.push(t.id);
        }
        let u = {id: c.id, ticketIds: newArray, ticketIdToUpdate: ticketId};
        column = u
      }
    });
    let tagIdsOfElement: string[] = [];
    this.interestingTickets.get(ticketId).tags.forEach(tag => {
      tagIdsOfElement.push(tag.id);
    });
    if (tagIdsOfElement.includes(targetTagId)) {
      let updateObs = this.apiCallService
        .callNoError<void>(p => this.kanbanBoardApi.updateKanbanBoardsUsingPUTWithHttpInfo(this.kanbanBoard.id, column, p));
      this.queue.push(updateObs).subscribe(result => {
        this.refresh(this.kanbanBoard.projectId, this.kanbanBoard.id).subscribe();
      });
    } else {
      let updateObs = this.apiCallService
        .callNoError<void>(p => this.kanbanBoardApi.updateKanbanBoardsUsingPUTWithHttpInfo(this.kanbanBoard.id, column, p));
      let obs = this.apiCallService
        .call<void>(p => this.ticketTagRelationApi.setTicketTagRelationUsingPUTWithHttpInfo(ticketId, targetTagId, p))
        .flatMap(result => {
          return updateObs;
        });
      this.queue.push(obs).subscribe(result => {
        this.refresh(this.kanbanBoard.projectId, this.kanbanBoard.id).subscribe();
      });
    }
  }

  ngOnInit(): void {
    this.route.params
      .do(() => {
        this.loading = true;
      })
      .switchMap(param => {
        this.route.queryParams.subscribe(p => {
          this.ticketFilter = new TicketFilter(p['title'] || undefined,
            p['ticketNumber'] || undefined, p['tag'] || undefined, p['user'] || undefined,
            p['progressOne'] || undefined, p['progressTwo'] || undefined, p['progressGreater'] || undefined,
            p['dueDateOne'] || undefined, p['dueDateTwo'] || undefined, p['dueDateGreater'] || undefined,
            p['spOne'] || undefined, p['spTwo'] || undefined, p['spGreater'] || undefined,
            p['open'] || undefined);
          this.query = this.ticketFilter.toTicketFilterString();
        }, error => {
        });

        let projectId = param['projectId'];
        let boardId = param['boardId'];
        return this.refresh(projectId, boardId);
      })
      .subscribe(() => {
        this.loading = false;
      });
    this.filterTerms.debounceTime(900)
      .switchMap(term => this.refresh(this.kanbanBoard.projectId, this.kanbanBoard.id)).subscribe(result => {
    }, error => {
    });
  }

  private refresh(projectId: string, boardId: string): Observable<void> {
    this.location.replaceState('/project/' + projectId + '/board/' + boardId + '?' + this.ticketFilter.toTicketFilterURLString());
    let kanbanBoardObs = this.apiCallService
      .callNoError<KanbanBoardResultJson>(p => this.kanbanBoardApi.getKanbanBoardUsingGETWithHttpInfo(boardId, p));


    let kanbanColumnObs = this.apiCallService
      .callNoError<KanbanColumnResultJson[]>(p => this.kanbanBoardApi.listKanbanColumnsUsingGETWithHttpInfo(boardId,
        this.ticketFilter.ticketNumbers, this.ticketFilter.title, this.ticketFilter.tags, this.ticketFilter.users,
        this.ticketFilter.progressOne, this.ticketFilter.progressTwo, this.ticketFilter.progressGreater,
        this.ticketFilter.dueDateOne, this.ticketFilter.dueDateTwo, this.ticketFilter.dueDateGreater,
        this.ticketFilter.storyPointsOne, this.ticketFilter.storyPointsTwo, this.ticketFilter.storyPointsGreater,
        this.ticketFilter.open, p));

    let ticketTagsObs = this.apiCallService
      .callNoError<TicketTagResultJson[]>(p => this.ticketTagsApi.listTicketTagsUsingGETWithHttpInfo(null, projectId, p))
      .map(tts => idListToMap(tts).map(tt => new KanbanDetailTag(tt)).toMap());

    let projectUsersObs = this.apiCallService
      .callNoError<UserResultJson[]>(p => this.projectApi.listProjectUsersUsingGETWithHttpInfo(projectId, p))
      .map(users => idListToMap(users).map(user => new TicketOverviewUser(user)).toMap());


    return Observable
      .zip(kanbanColumnObs, ticketTagsObs, kanbanBoardObs, projectUsersObs)
      .flatMap(tuple => {
        this.allProjectUsers = tuple[3];
        let columnResults = tuple[0];
        let wantedTicketIds: string[] = [];
        columnResults.forEach(c => {
          c.ticketIds.forEach(id => {
            if (!wantedTicketIds.includes(id)) {
              wantedTicketIds.push(id);
            }
          });
        });

        let getObs = this.apiCallService
          .callNoError<GetResultJson>(p => this.getApi.getUsingPOSTWithHttpInfo({
            ticketIds: wantedTicketIds,
            ticketIdsForStatistic: wantedTicketIds
          }, p));
        return Observable.zip(Observable.of(tuple), getObs);
      })
      .flatMap(tuple => {
        let ticketsResult = tuple[1];
        let wantedUserIds: string[] = [];
        let wantedSubticketsIds: string[] = [];
        for (let key in ticketsResult.tickets) {
          if (ticketsResult.tickets.hasOwnProperty(key)) {
            let t = ticketsResult.tickets[key];
            t.ticketUserRelations.map(ta => wantedUserIds.push(ta.userId));
            wantedSubticketsIds.push(...t.subTicketIds);
          }
        }

        let getObs = this.apiCallService
          .callNoError<GetResultJson>(p => this.getApi.getUsingPOSTWithHttpInfo({
            userIds: wantedUserIds,
            ticketIds: wantedSubticketsIds
          }, p));

        return Observable.zip(Observable.of(tuple[0]), Observable.of(tuple[1]), getObs);
      })
      .do(tuple => {
        this.allTicketTags = tuple[0][1];
        this.allTicketTagsForFilter = tuple[0][1];
        this.kanbanBoard = new KanbanBoard(tuple[0][2]);
        this.interestingUsers = imm.Map(tuple[2].users).map(u => new TicketDetailUser(u)).toMap();
        this.relatedProgresses = imm.Map(tuple[1].ticketStatistics).map((p, tid) => new TicketDetailProgress(tid, p)).toMap();
        let relatedSubTickets = imm.Map(tuple[2].tickets).map(t => newTicketDetailRelated(t, this.relatedProgresses)).toMap();
        this.interestingTickets = imm.Map(tuple[1].tickets)
          .map(t => new KanbanDetailTicket(t, this.interestingUsers,
            this.allTicketTags, this.relatedProgresses, relatedSubTickets)).toMap();
        this.kanbanColumns = imm.List(tuple[0][0]).map(c => new KanbanDetailColumn(c, this.interestingTickets)).toList();
      })
      .map(it => undefined)
      .catch(err => Observable.empty<void>());

  }

  updateFilter(event: TicketFilter) {
    this.ticketFilter = event;
    this.filterTerms.next(event);

  }

  onFindSubTickets(event: FindSubTicketEvent) {
    this.ticketFilter = new TicketFilter(undefined, event.subTicketIds, undefined, undefined, undefined,
      undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined);
    this.query = this.ticketFilter.toTicketFilterString();
  }
}


export class KanbanDetailColumn implements Tag {
  readonly color: string;
  readonly id: string;
  readonly kanbanBoardId: string;
  readonly name: string;
  readonly normalizedName: string;
  readonly order: number;
  readonly tickets: KanbanDetailTicket[];

  constructor(c: KanbanColumnResultJson,
              tickets: imm.Map<string, KanbanDetailTicket>) {
    this.color = c.color;
    this.id = c.id;
    this.kanbanBoardId = c.kanbanBoardId;
    this.name = c.name;
    this.normalizedName = c.normalizedName;
    this.order = c.order;
    this.tickets = c.ticketIds
      .map(id => tickets.get(id));
    Object.freeze(this);
  }

}
Object.freeze(KanbanDetailColumn.prototype);


export class KanbanDetailTag implements Tag {
  readonly id: string;
  readonly name: string;
  readonly normalizedName: string;
  readonly order: number;
  readonly color: string;

  constructor(tag: TicketTagResultJson) {
    this.id = tag.id;
    this.name = tag.name;
    this.normalizedName = tag.normalizedName;
    this.order = tag.order;
    this.color = tag.color;
    Object.freeze(this);
  }
}
Object.freeze(KanbanDetailTag.prototype);

export class KanbanDetailTicket {
  readonly createTime: number;
  readonly currentEstimatedTime: number|undefined;
  readonly dueDate: number|undefined;
  readonly description: string;
  readonly id: string;
  readonly initialEstimatedTime: number|undefined;
  readonly number: number;
  readonly open: boolean;
  readonly storyPoints: number|undefined;
  readonly tags: imm.List<KanbanDetailTag>;
  readonly title: string;
  readonly users: imm.List<TicketDetailUser>;
  readonly projectId: string;
  readonly progress: TicketDetailProgress|undefined;
  readonly subtickets: imm.List<TicketDetailRelated>;

  constructor(ticket: TicketResultJson,
              users: imm.Map<string, TicketDetailUser>,
              ticketTags: imm.Map<string, KanbanDetailTag>,
              relatedProgresses: imm.Map<string, TicketDetailProgress>,
              relatedTickets: imm.Map<string, TicketDetailRelated>) {
    this.createTime = ticket.createTime;
    this.currentEstimatedTime = ticket.currentEstimatedTime;
    this.dueDate = ticket.dueDate;
    this.description = ticket.description;
    this.id = ticket.id;
    this.initialEstimatedTime = ticket.initialEstimatedTime;
    this.number = ticket.number;
    this.open = ticket.open;
    this.storyPoints = ticket.storyPoints;

    this.tags = imm.List(ticket.tagIds)
      .map(tid => ticketTags.get(tid))
      .sort((a, b) => (a.order < b.order) ? -1 : (a.order === b.order ? 0 : 1))
      .toList();
    this.title = ticket.title;
    let tempUserIds: string[] = [];
    ticket.ticketUserRelations.forEach(r => {
      if (!tempUserIds.includes(r.userId)) {
        tempUserIds.push(r.userId);
      }
    });
    this.users = imm.List(tempUserIds)
      .map(id => users.get(id))
      .toList();
    this.projectId = ticket.projectId;
    this.progress = relatedProgresses.get(ticket.id);
    this.subtickets = imm.Seq(ticket.subTicketIds)
      .map(id => relatedTickets.get(id))
      .filter(t => !!t)
      .toList();
  }
}
Object.freeze(KanbanDetailTicket.prototype);

