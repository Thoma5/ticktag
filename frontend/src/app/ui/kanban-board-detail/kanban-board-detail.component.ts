import {Component, OnInit} from '@angular/core';
import * as imm from 'immutable';
import {ApiCallService} from '../../service/api-call/api-call.service';
import {BoardApi} from '../../api/api/BoardApi';
import {Router, ActivatedRoute} from '@angular/router';
import { Observable } from 'rxjs';
import {UserResultJson} from '../../api/model/UserResultJson';
import {Tag} from '../../util/taginput/taginput.component';
import {TicketTagResultJson} from '../../api/model/TicketTagResultJson';
import {TicketResultJson} from '../../api/model/TicketResultJson';
import {KanbanColumnResultJson} from '../../api/model/KanbanColumnResultJson';
import {GetResultJson} from '../../api/model/GetResultJson';
import {GetApi} from '../../api/api/GetApi';
import {TickettagApi} from '../../api/api/TickettagApi';
import { idListToMap } from '../../util/listmaputils';
import {KanbanBoard} from '../kanban-boards/kanban-boards.component';
import {KanbanBoardReslutJson} from '../../api/model/KanbanBoardReslutJson';
import {TicketDetailProgress} from '../ticket-detail/ticket-detail';
import {DragulaService} from 'ng2-dragula';
import {UpdateKanbanColumnJson} from '../../api/model/UpdateKanbanColumnJson';
import {TaskQueue} from '../../util/task-queue';
import {TickettagrelationApi} from "../../api/api/TickettagrelationApi";

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
  private interestingUsers: imm.Map<string, KanbanDetailUser>;
  private relatedProgresses: imm.Map<string, TicketDetailProgress>;

  private loading = true;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private apiCallService: ApiCallService,
              private getApi: GetApi,
              private ticketTagsApi: TickettagApi,
              private kanbanBoardApi: BoardApi,
              private dragulaService: DragulaService,
              private ticketTagRelationApi: TickettagrelationApi
  ) {
    dragulaService.dropModel.subscribe((value) => {
      this.onDropModel(value.slice(1));
    });
  }

  shouldBeHidden(ticket:KanbanDetailTicket):boolean{
    return ticket.number %2 ==0;
  }

  private onDropModel(args) {
    let target:HTMLDivElement = args[1];
    let el:HTMLDivElement = args[0]
    this.updateModel(el.getAttribute("id"),target.getAttribute("id"));
  }

  private updateModel(ticketId: string,targetTagId: string) {
    let columns: UpdateKanbanColumnJson[] = [];
    this.kanbanColumns.forEach(c => {
      if (c.id === targetTagId) {
        let newArray: string[] = [];
        for (let t of c.tickets) {
          newArray.push(t.id);
        }
        let u = {id: c.id, ticketIds: newArray};
        columns.push(u);
      }
    });
    let updateObs = this.apiCallService.callNoError<void>(p => this.kanbanBoardApi.updateKanbanBoardsUsingPUTWithHttpInfo(this.kanbanBoard.id, columns, p));
    let obs = this.apiCallService
      .call<void>(p => this.ticketTagRelationApi.setTicketTagRelationUsingPUTWithHttpInfo(ticketId, targetTagId, p))
      .flatMap(result => {
        return updateObs;
      });
    this.queue.push(obs).subscribe(result => {
      this.refresh(this.kanbanBoard.projectId, this.kanbanBoard.id).subscribe();
    });
  }

  ngOnInit(): void {
    this.route.params
      .do(() => { this.loading = true; })
      .switchMap(params => {
        let projectId = params['projectId'];
        let boardId = params['boardId'];
        return this.refresh(projectId, boardId);
      })
      .subscribe(() => {
        this.loading = false;
      });
  }

  private refresh(projectId: string, boardId: string): Observable<void> {
    let kanbanBoardObs = this.apiCallService
      .callNoError<KanbanBoardReslutJson>(p => this.kanbanBoardApi.getKanbanBoardUsingGETWithHttpInfo(boardId, p));

    let kanbanColumnObs = this.apiCallService
      .callNoError<KanbanColumnResultJson[]>(p => this.kanbanBoardApi.listKanbanColumnsUsingGETWithHttpInfo(boardId, p));

    let ticketTagsObs = this.apiCallService
      .callNoError<TicketTagResultJson[]>(p => this.ticketTagsApi.listTicketTagsUsingGETWithHttpInfo(null, projectId, p))
      .map(tts => idListToMap(tts).map(tt => new KanbanDetailTag(tt)).toMap());
    return Observable
      .zip(kanbanColumnObs, ticketTagsObs, kanbanBoardObs)
      .flatMap(tuple => {
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
            ticketIdsForStatistic: wantedTicketIds}, p));
        return Observable.zip(Observable.of(tuple), getObs);
      })
      .flatMap(tuple => {
        let ticketsResult = tuple[1];
        let wantedUserIds: string[] = [];
        for (let key in ticketsResult.tickets) {
          if (ticketsResult.tickets.hasOwnProperty(key)) {
            let t = ticketsResult.tickets[key];
            t.ticketUserRelations.map(ta => wantedUserIds.push(ta.userId));
          }
        }

        let getObs = this.apiCallService
          .callNoError<GetResultJson>(p => this.getApi.getUsingPOSTWithHttpInfo({ userIds: wantedUserIds }, p));

        return Observable.zip(Observable.of(tuple[0]), Observable.of(tuple[1]), getObs);
      })
      .do(tuple => {
        this.allTicketTags = tuple[0][1];
        this.kanbanBoard = new KanbanBoard(tuple[0][2]);
        this.interestingUsers = imm.Map(tuple[2].users).map(u => new KanbanDetailUser(u)).toMap();
        this.relatedProgresses = imm.Map(tuple[1].ticketStatistics).map((p, tid) => new TicketDetailProgress(tid, p)).toMap();
        this.interestingTickets = imm.Map(tuple[1].tickets)
          .map(t => new KanbanDetailTicket(t, this.interestingUsers, this.allTicketTags, this.relatedProgresses)).toMap();
        this.kanbanColumns = imm.List(tuple[0][0]).map(c => new KanbanDetailColumn(c, this.interestingTickets)).toList();
      })
      .map(it => undefined);
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

export class KanbanDetailUser {
  readonly id: string;
  readonly mail: string;
  readonly name: string;
  readonly username: string;

  constructor(user: UserResultJson) {
    this.id = user.id;
    this.mail = user.mail;
    this.name = user.name;
    this.username = user.username;
    Object.freeze(this);
  }
}
Object.freeze(KanbanDetailUser.prototype);

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
  readonly users: imm.List<KanbanDetailUser>;
  readonly projectId: string;
  readonly progress: TicketDetailProgress|undefined;

  constructor(
    ticket: TicketResultJson,
    users: imm.Map<string, KanbanDetailUser>,
    ticketTags: imm.Map<string, KanbanDetailTag>,
    relatedProgresses: imm.Map<string, TicketDetailProgress>) {
    this.createTime = ticket.createTime;
    this.currentEstimatedTime = ticket.currentEstimatedTime;
    this.dueDate = ticket.dueDate;
    this.description = ticket.description;
    this.id = ticket.id;
    this.initialEstimatedTime = ticket.initialEstimatedTime;
    this.number = ticket.number;
    this.open = ticket.open;
    this.storyPoints = ticket.storyPoints;
    this.tags =  imm.List(ticket.tagIds)
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
  }
}
Object.freeze(KanbanDetailTicket.prototype);

