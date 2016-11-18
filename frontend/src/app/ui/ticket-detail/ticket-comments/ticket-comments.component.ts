import { Component, Input } from '@angular/core';
import { Comment } from '../ticket-detail.component';

@Component({
    selector: 'tt-ticket-comments',
    templateUrl: './ticket-comments.component.html',
    styleUrls: ['./ticket-comments.component.scss']
})
export class TicketCommentsComponent {
    @Input() comments: Comment[];
}
