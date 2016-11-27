import { Component, Input, EventEmitter, Output, OnChanges, SimpleChanges } from '@angular/core';
import { UserApi, UserResultJson } from '../../../api';
import { ApiCallService } from '../../../service';
import { using } from '../../../util/using';
import * as imm from 'immutable';
import { TicketDetail, TicketDetailAssTag, TicketDetailUser } from '../ticket-detail';

type Assignment = {
  user: TicketDetailUser,
  tags: imm.List<{ id: string, transient: boolean }>
}

@Component({
  selector: 'tt-ticket-sidebar',
  templateUrl: './ticket-sidebar.component.html',
  styleUrls: ['./ticket-sidebar.component.scss']
})
export class TicketSidebarComponent implements OnChanges {
  @Input() ticket: TicketDetail;
  @Input() allAssignmentTags = imm.Map<string, TicketDetailAssTag>();
  private assignments: imm.List<Assignment>;

  @Output() readonly assignmentAdd = new EventEmitter<{user: string, tag: string}>();
  @Output() readonly assignmentRemove = new EventEmitter<{user: string, tag: string}>();
  @Output() readonly userAdd = new EventEmitter<TicketDetailUser>();

  private adding = false;
  private checking = false;
  private newUserName = '';

  constructor(
    private userApi: UserApi,
    private apiCallService: ApiCallService) {
  }

  ngOnChanges(changes: SimpleChanges) {
    if ('ticket' in changes || 'allAssignmentTags' in changes) {
      this.assignments = this.ticket.users
        .map((tags, user) => ({
          user: user,
          tags: tags.map(t => ({id: t.tag.id, transient: t.transient})).toList()
        }))
        .sort(using<Assignment>(it => it.user.name.toLocaleLowerCase()))
        .toList();
    }
  }

  onTagAdd(userId: string, tagId: string) {
    this.assignmentAdd.emit({ user: userId, tag: tagId });
  }

  onTagRemove(userId: string, tagId: string) {
    this.assignmentRemove.emit({ user: userId, tag: tagId });
  }

  onShowAdd() {
    this.adding = true;
  }

  onHideAdd() {
    this.adding = false;
  }

  onAdd() {
    this.checking = true;
    let username = this.newUserName.trim();
    if (username) {
      // TODO graceful error handling
      this.apiCallService
        .callNoError<UserResultJson>(p => this.userApi.getUserByUsernameUsingGETWithHttpInfo(username, p))
        .map(user => new TicketDetailUser(user))
        .subscribe(user => {
          this.checking = false;
          this.userAdd.emit(user);
          this.newUserName = '';
        });
    }
  }

  assignedUserTrackBy(index: number, item: Assignment): string {
    return item.user.id;
  }
}
