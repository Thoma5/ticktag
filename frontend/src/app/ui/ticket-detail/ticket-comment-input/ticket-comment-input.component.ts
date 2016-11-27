import { Component, Input, Output, EventEmitter } from '@angular/core';
import {
  TicketDetail, TicketDetailTag, TicketDetailAssTag, TicketDetailTimeCategory
} from '../ticket-detail';
import * as imm from 'immutable';
import { CommentTextviewSaveEvent } from '../command-textview/command-textview.component';
import { Cmd } from '../command-textview/grammar';

@Component({
  selector: 'tt-ticket-comment-input',
  templateUrl: './ticket-comment-input.component.html',
  styleUrls: ['./ticket-comment-input.component.scss']
})
export class TicketCommentInputComponent {
  @Input() ticket: TicketDetail;
  @Input() allTicketTags: imm.Map<string, TicketDetailTag>;
  @Input() allAssignmentTags: imm.Map<string, TicketDetailAssTag>;
  @Input() allTimeCategories: imm.Map<string, TicketDetailTimeCategory>;

  @Output() commentCreate = new EventEmitter<CommentTextviewSaveEvent>();

  content: CommentTextviewSaveEvent = { commands: imm.List<Cmd>(), text: '' };

  onSubmit(): void {
    if (this.content != null) {
      this.commentCreate.emit(this.content);
    }
  }
}
