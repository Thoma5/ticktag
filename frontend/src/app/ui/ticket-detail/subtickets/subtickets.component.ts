import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { TicketDetail, TicketDetailRelated } from '../ticket-detail';
import { TicketRestoreEvent } from '../subticket/subticket.component';
import * as imm from 'immutable';

@Component({
  selector: 'tt-subtickets',
  templateUrl: './subtickets.component.html',
  styleUrls: ['./subtickets.component.scss'],
})
export class SubticketsComponent implements OnChanges {
  @Input() parentTicket: TicketDetail;
  @Input() tickets: imm.List<TicketDetailRelated>;

  @Output() readonly ticketRestore = new EventEmitter<TicketRestoreEvent>();

  private sortedTickets: imm.List<TicketDetailRelated>;

  ngOnChanges(changes: SimpleChanges) {
    if ('tickets' in changes) {
      this.sortedTickets = this.tickets.sort((a, b) => a.number - b.number).toList();
    }
  }
}
