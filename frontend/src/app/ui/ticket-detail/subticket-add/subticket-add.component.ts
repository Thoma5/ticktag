import { Component, Input, Output, EventEmitter } from '@angular/core';
import {
  TicketDetailAssTag, TicketDetail, TicketDetailTimeCategory, TicketDetailTag
} from '../ticket-detail';
import * as imm from 'immutable';

@Component({
  selector: 'tt-subticket-add',
  templateUrl: './subticket-add.component.html',
  styleUrls: ['./subticket-add.component.scss']
})
export class SubticketAddComponent {
  // TODO
  @Input() parentTicket: TicketDetail;
  @Input() allTicketTags: imm.Map<string, TicketDetailTag>;
  @Input() allTimeCategories: imm.Map<string, TicketDetailTimeCategory>;
  @Input() allAssignmentTags: imm.Map<string, TicketDetailAssTag>;
  @Output() ticketAdd = new EventEmitter<void>();

  editing: boolean = false;
  title: string | null = null;

  startEditing() {
    this.editing = true;
  }

  finishEditing() {
    this.editing = false;
  }
}
