import { Component, Input } from '@angular/core';
import {TicketEventResultJson} from '../../../../api/model/TicketEventResultJson';

@Component({
  selector: 'tt-ticket-event-title-changed',
  templateUrl: './ticket-event-title-changed.component.html',
  styleUrls: ['../ticket-events.component.scss']
})
export class TicketEventTitleChangedComponent {
    @Input() event: TicketEventResultJson;

    dateFromEvent() {
      return new Date(this.event.time);
    }
}
