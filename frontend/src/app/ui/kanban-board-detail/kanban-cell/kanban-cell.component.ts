import {Component, Input} from '@angular/core';
import {KanbanDetailTicket} from '../kanban-board-detail.component';

@Component({
  selector: 'tt-kanban-cell',
  templateUrl: './kanban-cell.component.html',
  styleUrls: ['./kanban-cell.component.scss']
})
export class KanbanCellComponent {
  @Input() ticket: KanbanDetailTicket;
}


