import { Component, Input } from '@angular/core';
import {TicketEventResultJson} from '../../../api/model/TicketEventResultJson';
import * as imm from 'immutable'

@Component({
    selector: 'tt-ticket-events',
    templateUrl: './ticket-events.component.html',
    styleUrls: ['./ticket-events.component.scss']
})
export class TicketEventsComponent {
    @Input() events: imm.List<TicketEventResultJson>;
}
