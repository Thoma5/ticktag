import { Component, Input, EventEmitter, Output } from '@angular/core';
import { TicketDetailAssTag, TicketDetailUser } from '../ticket-detail';
import * as imm from 'immutable';

@Component({
  selector: 'tt-assigned-user',
  templateUrl: './assigned-user.component.html',
  styleUrls: ['./assigned-user.component.scss']
})
export class AssignedUserComponent {
  @Input() user: TicketDetailUser;
  @Input() tags: string[];
  @Input() allTags: imm.Map<string, TicketDetailAssTag>;

  @Output() readonly tagAdd = new EventEmitter<string>();
  @Output() readonly tagRemove = new EventEmitter<string>();
}
