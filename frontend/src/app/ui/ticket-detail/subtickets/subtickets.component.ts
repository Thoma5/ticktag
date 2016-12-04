import { Component, Input, Output, EventEmitter } from '@angular/core';
import { TicketDetail, TicketDetailRelated } from '../ticket-detail';
import { TicketRestoreEvent } from '../subticket/subticket.component';
import * as imm from 'immutable';

@Component({
  selector: 'tt-subtickets',
  templateUrl: './subtickets.component.html',
  styleUrls: ['./subtickets.component.scss']
})
export class SubticketsComponent {
  @Input() parentTicket: TicketDetail;
  @Input() tickets: imm.List<TicketDetailRelated>;

  @Output() readonly ticketRestore = new EventEmitter<TicketRestoreEvent>();
}
