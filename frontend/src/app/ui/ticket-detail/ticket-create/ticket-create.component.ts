import { Component, Input, Output, EventEmitter } from '@angular/core';
import * as imm from 'immutable';
import { CommandTextviewTicketTag, CommandTextviewTimeCategory, CommandTextviewAssignmentTag,
  CommandTextviewSaveEvent } from '../../../util/command-textview/command-textview.component';
import * as grammar from '../../../service/command/grammar';

export type TicketCreateEvent = {
  projectId: string,
  title: string,
  description: string,
  commands: imm.List<grammar.Cmd>,
  navigate: boolean
};

@Component({
  selector: 'tt-ticket-create',
  templateUrl: './ticket-create.component.html',
  styleUrls: ['./ticket-create.component.scss']
})
export class TicketCreateComponent {
  @Input() projectId: string;
  @Input() allTicketTags: imm.Map<string, CommandTextviewTicketTag>;
  @Input() allTimeCategories: imm.Map<string, CommandTextviewTimeCategory>;
  @Input() allAssignmentTags: imm.Map<string, CommandTextviewAssignmentTag>;
  readonly activeTags = imm.List.of();
  readonly assignedUsers = imm.List.of();

  @Input() working = false;

  @Output() readonly ticketAdd = new EventEmitter<TicketCreateEvent>();

  description: CommandTextviewSaveEvent = this.getEmptyDescription();
  title: string = '';

  onSubmit(navigate: boolean) {
    this.ticketAdd.emit({
      projectId: this.projectId,
      title: this.title,
      description: this.description.text,
      commands: this.description.commands,
      navigate: navigate
    });
  }

  private getEmptyDescription(): CommandTextviewSaveEvent {
    return { commands: imm.List.of<grammar.Cmd>(), text: '' };
  }
}
