import { Component, Input } from '@angular/core';
import { TicketDetailComment } from '../ticket-detail';

@Component({
  selector: 'tt-ticket-comment',
  templateUrl: './ticket-comment.component.html',
  styleUrls: ['./ticket-comment.component.scss']
})
export class TicketCommentComponent {
    @Input() comment: TicketDetailComment;
}
