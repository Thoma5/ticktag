import { Component, Input, Output, EventEmitter } from '@angular/core';
import * as imm from 'immutable';
import { CommentTextviewSaveEvent } from '../command-textview/command-textview.component';
import * as grammar from '../../../service/command/grammar';
import {
  TicketDetailAssTag, TicketDetailTimeCategory, TicketDetailTag
} from '../ticket-detail';

export type TicketCreateEvent = {
  projectId: string,
  title: string,
  description: string,
  commands: imm.List<grammar.Cmd>,
}

@Component({
  selector: 'tt-ticket-create',
  templateUrl: './ticket-create.component.html',
  styleUrls: ['./ticket-create.component.scss']
})
export class TicketCreateComponent {
  @Input() projectId: string;
  @Input() allTicketTags: imm.Map<string, TicketDetailTag>;
  @Input() allTimeCategories: imm.Map<string, TicketDetailTimeCategory>;
  @Input() allAssignmentTags: imm.Map<string, TicketDetailAssTag>;
  readonly activeTags = imm.List.of();
  readonly assignedUsers = imm.List.of();

  @Input() working = false;

  @Output() readonly ticketAdd = new EventEmitter<TicketCreateEvent>();

  description: CommentTextviewSaveEvent = this.getEmptyDescription();
  title: string = '';

  onSubmit() {
    this.ticketAdd.emit({
      projectId: this.projectId,
      title: this.title,
      description: this.description.text,
      commands: this.description.commands
    });
  }

  private getEmptyDescription(): CommentTextviewSaveEvent {
    return { commands: imm.List.of<grammar.Cmd>(), text: '' };
  }
}
