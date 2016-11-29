import { Component, Input } from '@angular/core';
import { TicketOverviewUser } from '../ticket-overview';

@Component({
  selector: 'tt-assigned-user-overview',
  templateUrl: './assigned-user-overview.component.html',
  styleUrls: ['./assigned-user-overview.component.scss']
})
export class AssignedUserOverviewComponent {
  @Input() user: TicketOverviewUser;
}
