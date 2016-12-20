import {Component, OnInit} from '@angular/core';
import * as imm from 'immutable';
import {KanbanBoardResultJson} from '../../api/model/KanbanBoardResultJson';
import {ApiCallService} from '../../service/api-call/api-call.service';
import {BoardApi} from '../../api/api/BoardApi';
import {Router, ActivatedRoute} from '@angular/router';
import { Observable } from 'rxjs';

@Component({
  selector: 'tt-kanban-boards',
  templateUrl: './kanban-boards.component.html',
  styleUrls: ['./kanban-boards.component.scss']
})
export class KanbanBoardsComponent implements OnInit {
  private kanbanBoards: imm.List<KanbanBoard>;
  private loading = true;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private apiCallService: ApiCallService,
              private kanbanBoardApi: BoardApi) {}

  ngOnInit(): void {
    this.route.params
      .do(() => { this.loading = true; })
      .switchMap(params => {
        let projectId = params['projectId'];
        return this.refresh(projectId);
      })
      .subscribe(() => {
        this.loading = false;
      });
  }

  private refresh(projectId: string): Observable<void> {
    let kanbanBoardsObs = this.apiCallService
      .callNoError<KanbanBoardResultJson>(p => this.kanbanBoardApi.listKanbanBoardsUsingGETWithHttpInfo(projectId, p));
    return Observable
      .zip(kanbanBoardsObs)
      .do(tuple => {
        this.kanbanBoards = imm.List(tuple[0]).map((b: KanbanBoardResultJson) => new KanbanBoard(b)).toList();
      })
      .map(it => undefined);
  }
}

export class KanbanBoard {
  readonly id: string;
  readonly name: string;
  readonly projectId: string;

  constructor(b: KanbanBoardResultJson) {
    this.id = b.id;
    this.name = b.name;
    this.projectId = b.projectId;
    Object.freeze(this);
  }
}
Object.freeze(KanbanBoard.prototype);
