import { Component, Input, Output, OnInit, EventEmitter } from '@angular/core';
import { TicketOverviewUser, TicketOverviewAssignment } from '../ticket-overview';
import * as imm from 'immutable';

@Component({
  selector: 'tt-assigned-user-overview',
  templateUrl: './assigned-user-overview.component.html',
  styleUrls: ['./assigned-user-overview.component.scss']
})
export class AssignedUserOverviewComponent implements OnInit {
  @Input() users: imm.Map<TicketOverviewUser, imm.List<TicketOverviewAssignment>>;
  @Output() clickedUser = new EventEmitter<TicketOverviewUser>();

  usersList: imm.List<TicketOverviewUser>;
  ready: boolean = false;

  ngOnInit(): void {
    this.usersList = this.users.keySeq().toList();
    this.ready = true;
  }

  userClicked(tag: TicketOverviewUser) {
    this.clickedUser.emit(tag);
  }
}
