import { Component, Input } from '@angular/core';
import * as imm from 'immutable';
import {TicketEvent} from '../ticket-detail';

@Component({
    selector: 'tt-ticket-events',
    templateUrl: './ticket-events.component.html',
    styleUrls: ['./ticket-events.component.scss']
})
export class TicketEventsComponent {
    @Input() events: imm.List<TicketEvent>;
}
