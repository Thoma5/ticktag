import { Component, Input } from '@angular/core';
import {TicketDetailRelated} from '../../ticket-detail';

@Component({
  selector: 'tt-ticket-event-parent-ticket-changed',
  templateUrl: './ticket-event-parent-ticket-changed.component.html',
  styleUrls: ['../ticket-events.component.scss', 'ticket-event-parent-ticket-changed.component.scss']
})
export class TicketEventParentChangedComponent {
    @Input() title: String;
    @Input() srcParent: TicketDetailRelated;
    @Input() dstParent: TicketDetailRelated;
}
