import { Component, Input, Output, EventEmitter } from '@angular/core';
import { TicketResultJson, TicketTagResultJson, UserResultJson } from '../../../api/';

@Component({
  selector: 'tt-ticket-core',
  templateUrl: './ticket-core.component.html',
  styleUrls: ['./ticket-core.component.scss']
})
export class TicketCoreComponent {
    _ticket: TicketResultJson;
    @Output() ticketChange: EventEmitter<TicketResultJson> = new EventEmitter<TicketResultJson>();
    @Input() allTags: TicketTagResultJson[];
    _ticketCreator: UserResultJson;
    @Output() ticketCreatorChange: EventEmitter<UserResultJson> = new EventEmitter<UserResultJson>();

    editingTitle: boolean;
    editingDescription: boolean;

    @Input()
    get ticket(): TicketResultJson {
      return this._ticket;
    }

    set ticket(val: TicketResultJson) {
      this._ticket = val;
      this.ticketChange.emit(this._ticket);
    }

    @Input()
    get ticketCreator(): UserResultJson {
      return this._ticketCreator;
    }

    set ticketCreator(val: UserResultJson) {
      this._ticketCreator = val;
      this.ticketCreatorChange.emit(this._ticketCreator);
    }
}
