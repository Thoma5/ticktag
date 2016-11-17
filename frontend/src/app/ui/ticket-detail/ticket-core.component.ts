import { Component, Input } from '@angular/core';
import { TicketResultJson } from './ticket-detail.component';

@Component({
  selector: 'tt-ticket-core',
  templateUrl: './ticket-core.component.html',
  styleUrls: ['./ticket-core.component.scss']
})
export class TicketCoreComponent {
    @Input() ticket: TicketResultJson;
}
