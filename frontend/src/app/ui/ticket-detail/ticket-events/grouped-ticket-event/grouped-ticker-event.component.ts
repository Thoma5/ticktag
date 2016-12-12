import { Component, Input, OnChanges, ElementRef } from '@angular/core';
import { TicketEvent } from '../../ticket-detail';
import { ImagesService } from '../../../../service';
import { UserApi } from '../../../../api';
import { Subscription } from 'rxjs';
import {GroupedTicketEvent} from '../ticket-events.component';

@Component({
  selector: 'tt-grouped-ticket-event',
  templateUrl: 'grouped-ticket-event.component.html',
  styleUrls: ['../ticket-events.component.scss']
})
export class GroupedTicketEventComponent {
  @Input() group: GroupedTicketEvent;

  firstEvent(): TicketEvent {
    return this.group.events[0];
  }
}
