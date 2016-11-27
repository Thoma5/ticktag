import { Component, Input, Output, EventEmitter } from '@angular/core';
import {
  TicketDetailAssTag, TicketDetail, TicketDetailTimeCategory, TicketDetailTag
} from '../ticket-detail';
import * as imm from 'immutable';
import { CommentTextviewSaveEvent } from '../command-textview/command-textview.component';
import { Cmd } from '../command-textview/grammar';

@Component({
  selector: 'tt-subticket-add',
  templateUrl: './subticket-add.component.html',
  styleUrls: ['./subticket-add.component.scss']
})
export class SubticketAddComponent {
  @Input() parentTicket: TicketDetail;
  @Input() allTicketTags: imm.Map<string, TicketDetailTag>;
  @Input() allTimeCategories: imm.Map<string, TicketDetailTimeCategory>;
  @Input() allAssignmentTags: imm.Map<string, TicketDetailAssTag>;
  readonly activeTags = imm.List.of();
  readonly assignedUsers = imm.List.of();

  @Output() readonly ticketAdd = new EventEmitter<void>();

  editing: boolean = false;
  title: string | null = null;
  description: CommentTextviewSaveEvent = { commands: imm.List.of<Cmd>(), text: '' };

  startEditing() {
    this.editing = true;
  }

  finishEditing() {
    this.editing = false;
  }

  onSubmit(): void {
    if (this.title != null && this.description != null) {
      // TODO
      this.ticketAdd.emit();
    }
  }
}
