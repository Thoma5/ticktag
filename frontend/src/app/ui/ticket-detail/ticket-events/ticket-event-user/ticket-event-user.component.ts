import { Component, Input } from '@angular/core';
import {TicketDetailAssTag, TicketDetailUser} from '../../ticket-detail';

@Component({
  selector: 'tt-ticket-event-user',
  templateUrl: './ticket-event-user.component.html',
  styleUrls: ['../ticket-events.component.scss']
})
export class TicketEventUserComponent {
  @Input() user: TicketDetailUser;
  @Input() tag: TicketDetailAssTag;
  @Input() added: Boolean;
}
