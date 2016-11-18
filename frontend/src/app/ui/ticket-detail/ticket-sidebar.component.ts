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
      {id: 'asdfasdfasdfasdf', name: 'Developer', color: 'ffff00', order: 1},
      {id: '0938rfgjhsd0wsafd', name: 'Reviewer', color: 'ff00ff', order: 2},
    ];
    allTags: AssignmentTagResultJson[] = [
      {id: 'asdfasdfasdfasdf', name: 'Developer', color: 'ffff00', order: 1},
      {id: '0938rfgjhsd0wsafd', name: 'Reviewer', color: 'ff00ff', order: 2},
      {id: '0sdfsfsdfsdfsdfasdfd', name: 'Owner', color: 'ffff00', order: 3},
      {id: '0938rfasdfasddf0wsafd', name: 'Blaa', color: 'ffffff', order: 4},
    ];
    referencedTickets: {number: number, title: string}[] = [
      {number: 234, title: 'Fix this ugly thing there'},
      {number: 4, title: 'World domination'},
      {number: 5587, title: 'Just do something... please!'},
    ];
    referencedByTickets: {number: number, title: string}[] = [
      {number: 234, title: 'Fix this ugly thing there'},
      {number: 78, title: 'This is a ticket'},
      {number: 999, title: 'Implement UI for tickets'},
      {number: 754, title: 'Automatically close super-ticket when all suptickets are done'},
    ];
}
