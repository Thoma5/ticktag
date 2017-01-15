import {Component, Input, Output, EventEmitter} from '@angular/core';
import {KanbanDetailTicket} from '../kanban-board-detail.component';
export type CollectEvent = {
  ticketId: string,
  tagId: string
};

export type FindSubTicketEvent = {
  subTicketIds: number[]
};

@Component({
  selector: 'tt-kanban-cell',
  templateUrl: './kanban-cell.component.html',
  styleUrls: ['./kanban-cell.component.scss']
})
export class KanbanCellComponent {
  @Input() ticket: KanbanDetailTicket;
  @Input() tagId: string;
  @Output() readonly collect = new EventEmitter<CollectEvent>();
  @Output() readonly findSubTickets = new EventEmitter<FindSubTicketEvent>();

  onSubmit() {
    this.collect.emit(
      {
        ticketId: this.ticket.id,
        tagId: this.tagId
      }
    );
  }

  onFindSubtickets() {
    let tempNumbers: number[] = [];
    this.ticket.subtickets.forEach(subticket => {
      tempNumbers.push(subticket.number);
    });
    tempNumbers.push(this.ticket.number);
    this.findSubTickets.emit(
      {subTicketIds: tempNumbers}
    );
  }

  hasSubtickets(): boolean {
    return !this.ticket.subtickets.isEmpty();
  }
}


