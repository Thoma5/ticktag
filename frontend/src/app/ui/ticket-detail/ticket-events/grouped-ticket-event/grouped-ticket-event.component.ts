import { Component, Input } from '@angular/core';
import { TicketEvent } from '../../ticket-detail';
import {GroupedTicketEvent} from '../ticket-events.component';

@Component({
  selector: 'tt-grouped-ticket-event',
  templateUrl: 'grouped-ticket-event.component.html',
  styleUrls: ['../ticket-events.component.scss']
})
export class GroupedTicketEventComponent {
  @Input() group: GroupedTicketEvent;
  @Input() projectId: string;

  firstEvent(): TicketEvent {
    return this.group.events[0];
  }
}
