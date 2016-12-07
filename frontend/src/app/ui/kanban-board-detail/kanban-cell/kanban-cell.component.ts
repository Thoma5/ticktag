import {Component, Input, ElementRef} from '@angular/core';
import {KanbanDetailTicket} from '../kanban-board-detail.component';
import {UserApi} from '../../../api/api/UserApi';
import {ImagesService} from '../../../service/images/images.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'tt-kanban-cell',
  templateUrl: './kanban-cell.component.html',
  styleUrls: ['./kanban-cell.component.scss']
})
export class KanbanCellComponent {
  @Input() ticket: KanbanDetailTicket;
}


