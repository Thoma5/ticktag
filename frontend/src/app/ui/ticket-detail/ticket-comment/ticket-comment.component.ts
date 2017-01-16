import { Component, Input, Output, EventEmitter } from '@angular/core';
import { TicketDetailComment, TicketDetailAssTag } from '../ticket-detail';
import * as imm from 'immutable';

@Component({
  selector: 'tt-ticket-comment',
  templateUrl: './ticket-comment.component.html',
  styleUrls: ['./ticket-comment.component.scss']
})
export class TicketCommentComponent {
  @Input() comment: TicketDetailComment;
  @Input() userTags: imm.List<TicketDetailAssTag>;
  @Input() projectId: string;
  @Input() transientTimes: imm.Map<string, boolean>;

  @Output() readonly undoTime = new EventEmitter<string>();
  @Output() readonly redoTime = new EventEmitter<string>();
}
