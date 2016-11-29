import { Component, Input } from '@angular/core';
import {TicketEvent} from '../../ticket-detail';

@Component({
  selector: 'tt-ticket-event',
  templateUrl: './ticket-event.component.html',
  styleUrls: ['../ticket-events.component.scss']
})
export class TicketEventComponent {
  @Input() event: TicketEvent;
}
