import { Component, Input } from '@angular/core';
import { UserResultJson, TicketResultJson } from '../../../api';
import { AssignmentTagResultJson } from '../assigned-user/assigned-user.component';

let userMock1: UserResultJson = {
  username: "maxmustermann",
  id: '123q4123412341324',
  mail: 'mail@maililili.com',
  name: 'Max Mustermann',
  role: UserResultJson.RoleEnum.USER,
};
let userMock2: UserResultJson = {
  username: "alan123",
  id: 'asdfasdfasdf',
  mail: 'mail@mailasdilili.com',
  name: 'Alan Turing',
  role: UserResultJson.RoleEnum.USER,
};
let tagsMock1 = ['asdfasdfasdfasdf', '0938rfgjhsd0wsafd'];
let tagsMock2 = ['0sdfsfsdfsdfsdfasdfd', '0938rfasdfasddf0wsafd'];
let allTagsMock: AssignmentTagResultJson[] = [
  {id: 'asdfasdfasdfasdf', name: 'Developer', color: 'ffff00', order: 1},
  {id: '0938rfgjhsd0wsafd', name: 'Reviewer', color: 'ff00ff', order: 2},
  {id: '0sdfsfsdfsdfsdfasdfd', name: 'Owner', color: 'ffff00', order: 3},
  {id: '0938rfasdfasddf0wsafd', name: 'Blaa', color: 'ffffff', order: 4},
];
let referencedTicketsMock: {number: number, title: string}[] = [
  {number: 234, title: 'Fix this ugly thing there'},
  {number: 4, title: 'World domination'},
  {number: 5587, title: 'Just do something... please!'},
];
let referencedByTicketsMock: {number: number, title: string}[] = [
  {number: 234, title: 'Fix this ugly thing there'},
  {number: 78, title: 'This is a ticket'},
  {number: 999, title: 'Implement UI for tickets'},
  {number: 754, title: 'Automatically close super-ticket when all suptickets are done'},
];

@Component({
  selector: 'tt-ticket-sidebar',
  templateUrl: './ticket-sidebar.component.html',
  styleUrls: ['./ticket-sidebar.component.scss']
})
export class TicketSidebarComponent {
    @Input() ticket: TicketResultJson;
    assigned = [{user: userMock1, tags: tagsMock1}, {user: userMock2, tags: tagsMock2}];
    allTags = allTagsMock;
    referencedTickets = referencedTicketsMock;
    referencedByTickets = referencedByTicketsMock;

    private adding = false;
    private newUserName = '';

    onShowAdd() {
      this.adding = true;
    }

    onHideAdd() {
      this.adding = false;
    }

    onAdd() {
      // TODO database checks and so on
      this.assigned.push({
        user: {
          id: Math.random() + '',
          mail: 'aa.aaa',
          name: this.newUserName,
          role: UserResultJson.RoleEnum.USER,
        },
        tags: [],
      });
      this.newUserName = '';
    }
}
