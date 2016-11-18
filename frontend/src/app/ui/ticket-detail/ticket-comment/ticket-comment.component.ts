import { Component, Input } from '@angular/core';
import { Comment } from '../ticket-detail.component';

@Component({
  selector: 'tt-ticket-comment',
  templateUrl: './ticket-comment.component.html',
  styleUrls: ['./ticket-comment.component.scss']
})
export class TicketCommentComponent {
    @Input() comment: Comment;
}
