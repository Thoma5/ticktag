import { Component, Input } from '@angular/core';

@Component({
  selector: 'tt-ticket-event-old-new',
  templateUrl: './ticket-event-old-new.component.html',
  styleUrls: ['../ticket-events.component.scss']
})
export class TicketEventOldNewComponent {
    @Input() oldString: String;
    @Input() newString: String;

}
