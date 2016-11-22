import { Component, Input } from '@angular/core';
import {
  TicketTagResultJson, AssignmentTagResultJson, TicketResultJson, TimeCategoryJson
} from '../../../api';

@Component({
  selector: 'tt-ticket-comment-input',
  templateUrl: './ticket-comment-input.component.html',
  styleUrls: ['./ticket-comment-input.component.scss']
})
export class TicketCommentInputComponent {
  @Input() ticket: TicketResultJson;
  @Input() allTicketTags = new Array<TicketTagResultJson>();
  @Input() allAssignmentTags = new Array<AssignmentTagResultJson>();
  @Input() allTimeCategories = new Array<TimeCategoryJson>();

  get ticketTags(): TicketTagResultJson[] {
    return this.ticket.tagIds.map(tid => this.allTicketTags.find(tt => tt.id === tid)).filter(tt => tt);
  }
}
