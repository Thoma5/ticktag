import { Component, Input } from '@angular/core';
import { CommentResultJson } from '../../../api';

@Component({
  selector: 'tt-ticket-comment',
  templateUrl: './ticket-comment.component.html',
  styleUrls: ['./ticket-comment.component.scss']
})
export class TicketCommentComponent {
    @Input() comment: CommentResultJson;
}
