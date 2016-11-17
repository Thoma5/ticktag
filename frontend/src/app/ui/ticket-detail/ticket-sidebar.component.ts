import { Component, Input } from '@angular/core';
import { TicketResultJson } from './ticket-detail.component';

@Component({
  selector: 'tt-ticket-sidebar',
  templateUrl: './ticket-sidebar.component.html',
  styleUrls: ['./ticket-sidebar.component.scss']
})
export class TicketSidebarComponent {
    @Input() ticket: TicketResultJson;


}
