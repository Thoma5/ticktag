import { Component, Input } from '@angular/core';
import { TicketEvent } from '../../ticket-detail';
import { UserApi } from '../../../../api';

@Component({
  selector: 'tt-ticket-event',
  templateUrl: './ticket-event.component.html',
  styleUrls: ['../ticket-events.component.scss']
})
export class TicketEventComponent {
  @Input() event: TicketEvent;
  readonly imagePath = '';

  constructor(private userApi: UserApi) {
    // This is a terrible, terrible hack to bypass the visibility modifier
    // But I don't know how else to get the base path
    this.imagePath = (<any>userApi).basePath + '/user/image';
  }
}
