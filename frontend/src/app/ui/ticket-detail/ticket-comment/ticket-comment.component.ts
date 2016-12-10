import { Component, Input, } from '@angular/core';
import { TicketDetailComment, TicketDetailAssTag } from '../ticket-detail';
import * as imm from 'immutable';
import { UserApi } from '../../../api';

@Component({
  selector: 'tt-ticket-comment',
  templateUrl: './ticket-comment.component.html',
  styleUrls: ['./ticket-comment.component.scss']
})
export class TicketCommentComponent {
  @Input() comment: TicketDetailComment;
  @Input() userTags: imm.List<TicketDetailAssTag>;
  readonly imagePath = '';

  constructor(private userApi: UserApi) {
    // This is a terrible, terrible hack to bypass the visibility modifier
    // But I don't know how else to get the base path
    this.imagePath = (<any>userApi).basePath + '/user/image';
  }
}
