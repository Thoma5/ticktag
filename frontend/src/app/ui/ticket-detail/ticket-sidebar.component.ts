import { Component, Input } from '@angular/core';
import { TicketResultJson } from './ticket-detail.component';
import { UserResultJson } from '../../api';
import { AssignmentTagResultJson } from './assigned-user.component';

@Component({
  selector: 'tt-ticket-sidebar',
  templateUrl: './ticket-sidebar.component.html',
  styleUrls: ['./ticket-sidebar.component.scss']
})
export class TicketSidebarComponent {
    @Input() ticket: TicketResultJson;
    user: UserResultJson = {
      id: '123q4123412341324',
      mail: 'mail@maililili.com',
      name: 'Max Mustermann',
      role: UserResultJson.RoleEnum.OBSERVER,
    };
    tags: AssignmentTagResultJson[] = [
      {id: 'asdfasdfasdfasdf', name: 'Developer', color: 'ffff00'},
      {id: '0938rfgjhsd0wsafd', name: 'Reviewer', color: 'ff00ff'},
    ];
}
