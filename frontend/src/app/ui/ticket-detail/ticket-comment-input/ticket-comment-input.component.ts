import { Component, Input } from '@angular/core';
import {
  TicketDetail, TicketDetailTag, TicketDetailAssTag, TicketDetailTimeCategory
} from '../ticket-detail';
import * as imm from 'immutable';

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
}
