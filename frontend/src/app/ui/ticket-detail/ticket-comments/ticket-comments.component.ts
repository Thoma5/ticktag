import { Component, Input } from '@angular/core';
import { TicketDetailComment } from '../ticket-detail';
import * as imm from 'immutable';

@Component({
    selector: 'tt-ticket-comments',
    templateUrl: './ticket-comments.component.html',
    styleUrls: ['./ticket-comments.component.scss']
})
export class TicketCommentsComponent {
    @Input() comments: imm.List<TicketDetailComment>;
}
