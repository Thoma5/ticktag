import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { TicketDetail, TicketDetailAssTag, TicketDetailComment, TicketDetailAssignment } from '../ticket-detail';
import * as imm from 'immutable';

type Comment = {
  comment: TicketDetailComment
  userTags: imm.List<TicketDetailAssTag>
}

@Component({
  selector: 'tt-ticket-comments',
  templateUrl: './ticket-comments.component.html',
  styleUrls: ['./ticket-comments.component.scss']
})
export class TicketCommentsComponent implements OnChanges {
  @Input() ticket: TicketDetail;
  @Input() allAssignmentTags: imm.List<TicketDetailAssTag>;

  private comments: imm.List<Comment>;

  ngOnChanges(changes: SimpleChanges): void {
    if ('ticket' in changes || 'allAssignmentTags' in changes) {
      this.comments = this.ticket.comments
        .map(comment => ({
          comment: comment,
          userTags: (this.ticket.users.get(comment.user) || imm.List<TicketDetailAssignment>())
            .map(at => at.tag)
            .sortBy(tag => tag.order)
            .toList()
        }))
        .sortBy(c => c.comment.createTime)
        .toList();
    }
  }
}
