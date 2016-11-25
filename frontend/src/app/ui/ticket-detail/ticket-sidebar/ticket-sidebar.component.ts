import { Component, Input, EventEmitter, Output, OnChanges, SimpleChanges } from '@angular/core';
import { TicketAssignmentJson } from '../../../api';
import * as imm from 'immutable';
import { TicketDetail, TicketDetailAssTag, TicketDetailUser } from '../ticket-detail';

@Component({
  selector: 'tt-ticket-sidebar',
  templateUrl: './ticket-sidebar.component.html',
  styleUrls: ['./ticket-sidebar.component.scss']
})
export class TicketSidebarComponent implements OnChanges {
  @Input() ticket: TicketDetail;
  @Input() allAssignmentTags = imm.Map<string, TicketDetailAssTag>();
  private assignments: imm.List<{ user: TicketDetailUser, tags: imm.List<string> }>;

  @Output() readonly assignmentAdd = new EventEmitter<TicketAssignmentJson>();
  @Output() readonly assignmentRemove = new EventEmitter<TicketAssignmentJson>();

  private adding = false;
  private newUserName = '';

  ngOnChanges(changes: SimpleChanges) {
    if ('ticket' in changes || 'allAssignmentTags' in changes) {
      this.assignments = this.ticket.users
        .map((tags, user) => ({ user: user, tags: tags.map(t => t.id).toList() }))
        .toList();
    }
  }

  onTagAdd(userId: string, tagId: string) {
    this.assignmentAdd.emit({ userId: userId, assignmentTagId: tagId });
  }

  onTagRemove(userId: string, tagId: string) {
    this.assignmentRemove.emit({ userId: userId, assignmentTagId: tagId });
  }

  onShowAdd() {
    this.adding = true;
  }

  onHideAdd() {
    this.adding = false;
  }

  onAdd() {
    if (this.newUserName) {
    }
    this.newUserName = '';
  }
}
