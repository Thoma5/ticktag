import {Component, Input, Output, EventEmitter} from '@angular/core';
import {KanbanDetailTicket} from '../kanban-board-detail.component';
export type CollectEvent = {
  ticketId: string,
  tagId: string
}
@Component({
  selector: 'tt-kanban-cell',
  templateUrl: './kanban-cell.component.html',
  styleUrls: ['./kanban-cell.component.scss']
})
export class KanbanCellComponent {
  @Input() ticket: KanbanDetailTicket;
  @Input() tagId: string;
  @Output() readonly collect = new EventEmitter<CollectEvent>();
  onSubmit(){
    this.collect.emit(
      {
        ticketId:this.ticket.id,
        tagId:this.tagId
      }
    );
  }
}


