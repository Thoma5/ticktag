import { Component, Input, Output, EventEmitter, AfterViewInit } from '@angular/core';
import {
  TicketDetailAssTag, TicketDetail, TicketDetailTimeCategory, TicketDetailTag
} from '../ticket-detail';
import * as imm from 'immutable';
import { CommentTextviewSaveEvent } from '../command-textview/command-textview.component';
import * as grammar from '../../../service/command/grammar';
import { Subject, ReplaySubject } from 'rxjs';

export type SubticketCreateEvent = {
  projectId: string,
  parentTicketId: string,
  title: string,
  description: string,
  commands: imm.List<grammar.Cmd>,
}

@Component({
  selector: 'tt-subticket-add',
  templateUrl: './subticket-add.component.html',
  styleUrls: ['./subticket-add.component.scss']
})
export class SubticketAddComponent implements AfterViewInit {
  @Input() parentTicket: TicketDetail;
  @Input() allTicketTags: imm.Map<string, TicketDetailTag>;
  @Input() allTimeCategories: imm.Map<string, TicketDetailTimeCategory>;
  @Input() allAssignmentTags: imm.Map<string, TicketDetailAssTag>;
  readonly activeTags = imm.List.of();
  readonly assignedUsers = imm.List.of();

  @Output() readonly ticketAdd = new EventEmitter<SubticketCreateEvent>();
  private readonly resetEventSubject = new Subject<string>();
  // Replays the last n (=1) events when subscribed, needed because the directive subscribes after our AfterViewInit event
  private readonly titleFocusEventSubject = new ReplaySubject<void>(1);

  editing: boolean = false;
  title: string = '';
  description: CommentTextviewSaveEvent = this.getEmptyDescription();

  ngAfterViewInit(): void {
    this.titleFocusEventSubject.next(undefined);
  }

  startEditing() {
    this.editing = true;
    this.title = '';
    this.description = this.getEmptyDescription();
    this.resetEventSubject.next(this.description.text);
    this.titleFocusEventSubject.next(undefined);
  }

  finishEditing(restart: boolean) {
    this.editing = false;
    if (restart) {
      this.startEditing();
    }
  }

  onSubmit(): void {
    let event = {
      projectId: this.parentTicket.projectId,
      parentTicketId: this.parentTicket.id,
      title: this.title,
      description: this.description.text,
      commands: this.description.commands,
    };
    this.ticketAdd.emit(event);
    this.finishEditing(true);
  }

  onAbort(): void {
    this.finishEditing(false);
  }

  private getEmptyDescription(): CommentTextviewSaveEvent {
    return { commands: imm.List.of<grammar.Cmd>(), text: '' };
  }
}
