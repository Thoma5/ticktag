import { Component, Input, Output, EventEmitter } from '@angular/core';
import {
  TicketDetail, TicketDetailTag, TicketDetailAssTag, TicketDetailTimeCategory
} from '../ticket-detail';
import * as imm from 'immutable';
import { CommentTextviewSaveEvent } from '../command-textview/command-textview.component';
import * as grammar from '../../../service/command/grammar';
import { Observable } from 'rxjs';

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
  @Input() working = false;
  @Input() resetEventObservable: Observable<string> = null;

  @Output() commentCreate = new EventEmitter<CommentTextviewSaveEvent>();

  content: CommentTextviewSaveEvent = { commands: imm.List<grammar.Cmd>(), text: '' };

  onSubmit(): void {
    if (this.content != null && !this.working) {
      this.commentCreate.emit(this.content);
    }
  }
}
