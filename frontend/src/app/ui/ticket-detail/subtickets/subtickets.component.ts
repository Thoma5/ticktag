import { Component, Input } from '@angular/core';
import { TicketDetailRelated } from '../ticket-detail';

@Component({
  selector: 'tt-subtickets',
  templateUrl: './subtickets.component.html',
  styleUrls: ['./subtickets.component.scss']
})
export class SubticketsComponent {
  @Input() tickets: TicketDetailRelated[];
}
