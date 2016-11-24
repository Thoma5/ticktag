import { Component, Input } from '@angular/core';
import {TicketEventResultJson} from '../../../../api/model/TicketEventResultJson';

@Component({
  selector: 'tt-ticket-event-title-changed',
  templateUrl: './ticket-event-title-changed.component.html',
  styleUrls: ['./ticket-event-title-changed.component.scss']
})
export class TicketEventTitleChangedComponent {
    @Input() event: TicketEventResultJson;
}
