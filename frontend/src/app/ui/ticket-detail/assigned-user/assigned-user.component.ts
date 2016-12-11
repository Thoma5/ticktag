import { Component, Input, EventEmitter, Output } from '@angular/core';
import { TicketDetailAssTag, TicketDetailUser } from '../ticket-detail';
import * as imm from 'immutable';
import { UserApi } from '../../../api';

@Component({
  selector: 'tt-assigned-user',
  templateUrl: './assigned-user.component.html',
  styleUrls: ['./assigned-user.component.scss']
})
export class AssignedUserComponent {
  @Input() user: TicketDetailUser;
  @Input() tags: imm.List<{ id: string, transient: boolean }>;
  @Input() allTags: imm.Map<string, TicketDetailAssTag>;

  @Output() readonly tagAdd = new EventEmitter<string>();
  @Output() readonly tagRemove = new EventEmitter<string>();

  readonly imagePath = '';

  constructor(private userApi: UserApi) {
    // This is a terrible, terrible hack to bypass the visibility modifier
    // But I don't know how else to get the base path
    this.imagePath = (<any>userApi).basePath + '/user/image';
  }
}
