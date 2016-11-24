import { Component, Input } from '@angular/core';
import { TicketResultJson, TicketTagResultJson, UserResultJson } from '../../../api/';

@Component({
  selector: 'tt-ticket-core',
  templateUrl: './ticket-core.component.html',
  styleUrls: ['./ticket-core.component.scss']
})
export class TicketCoreComponent {
    @Input() ticket: TicketResultJson;
    @Input() ticketTags: TicketTagResultJson[];
    @Input() ticketCreator: UserResultJson;
    editingTitle: boolean;
    editingDescription: boolean;
}
