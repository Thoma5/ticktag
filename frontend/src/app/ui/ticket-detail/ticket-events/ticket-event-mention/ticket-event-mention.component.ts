import { Component, Input } from '@angular/core';
import {TicketDetailRelated} from '../../ticket-detail';

@Component({
  selector: 'tt-ticket-event-mention',
  templateUrl: './ticket-event-mention.component.html',
  styleUrls: ['../ticket-events.component.scss', 'ticket-event-mention.component.scss']
})
export class TicketEventMentionComponent {
  @Input() ticket: TicketDetailRelated;
  @Input() added: Boolean;
}
