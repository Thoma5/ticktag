import { Component, Input, EventEmitter, Output, OnChanges, SimpleChanges } from '@angular/core';
import { UserResultJson, TicketResultJson, TicketAssignmentJson, AssignmentTagResultJson } from '../../../api';
import { using } from '../../../util/using';

interface Assignment {
  user: UserResultJson;
  tags: string[];
}

@Component({
  selector: 'tt-ticket-sidebar',
  templateUrl: './ticket-sidebar.component.html',
  styleUrls: ['./ticket-sidebar.component.scss']
})
export class TicketSidebarComponent implements OnChanges {
  @Input() ticket: TicketResultJson;
  @Input() allAssignmentTags = new Array<AssignmentTagResultJson>();
  @Input() assignedUsers = new Array<UserResultJson>();

  @Output() readonly assignmentAdd = new EventEmitter<TicketAssignmentJson>();
  @Output() readonly assignmentRemove = new EventEmitter<TicketAssignmentJson>();

  private adding = false;
  private newUserName = '';

  // Variables updated in ngOnChanges
  private assignments = new Array<Assignment>();

  ngOnChanges(changes: SimpleChanges): void {
    this.assignments = this.calculateAssignments();
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
  }

  private calculateAssignments(): Assignment[] {
    let map: {[key: string]: Assignment} = {};
    let result = new Array<Assignment>();

    // I love implementing left joins in JavaScript!
    for (let ta of this.ticket.ticketAssignments) {
      if (!map[ta.userId]) {
        let user = this.assignedUsers.find(u => u.id === ta.userId);
        if (!user) {
          continue;
        }
        map[ta.userId] = { user: user, tags: [] };
        result.push(map[ta.userId]);
      }
      let entry = map[ta.userId];
      if (!entry) {
        continue;
      }
      let tag = this.allAssignmentTags.find(tag => tag.id === ta.assignmentTagId);
      if (!tag) {
        continue;
      }
      entry.tags.push(tag.id);
    }

    result.sort(using<Assignment>(a => a.user.name));
    return result;
  }
}
