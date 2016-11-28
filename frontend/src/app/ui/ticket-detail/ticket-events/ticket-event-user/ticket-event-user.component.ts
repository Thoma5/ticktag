import { Component, Input } from '@angular/core';

@Component({
  selector: 'tt-ticket-event-user',
  templateUrl: './ticket-event-user.component.html',
  styleUrls: ['../ticket-events.component.scss']
})
export class TicketEventUserComponent {
  @Input() userId: String;
  @Input() userName: String;
  @Input() tagId: String;
  @Input() tagName: String;
  @Input() added: Boolean;

  getTitle() {
    return (this.added) ? 'User added' : 'User removed';
  }
}
