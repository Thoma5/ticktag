import { Component, Input } from '@angular/core';
import { TicketDetailTag } from '../../ticket-detail';

@Component({
  selector: 'tt-ticket-event-tag',
  templateUrl: './ticket-event-tag.component.html',
  styleUrls: ['../ticket-events.component.scss']
})
export class TicketEventTagComponent {
  @Input() tag: TicketDetailTag;
  @Input() added: Boolean;
}
