import { Component, Input } from '@angular/core';
import * as imm from 'immutable';
import {TicketEvent, TicketDetailUser} from '../ticket-detail';

@Component({
    selector: 'tt-ticket-events',
    templateUrl: './ticket-events.component.html',
    styleUrls: ['./ticket-events.component.scss']
})
export class TicketEventsComponent {
  groupedEvents: GroupedTicketEvent[];

  private _events: imm.List<TicketEvent>;
  get events(): imm.List<TicketEvent> { return this._events; }

  @Input('events')
  set events(events: imm.List<TicketEvent>) {
    this._events = events;

    let currentGroup: GroupedTicketEvent;
    this.groupedEvents = [];
    events.forEach(e => {
      if (!TicketEventsComponent.canBeGrouped(currentGroup, e)) {
        currentGroup = new GroupedTicketEvent(e.user);
        this.groupedEvents.push(currentGroup);
      }
      currentGroup.events.push(e);
    });
  }

  private static canBeGrouped(currentGroup: GroupedTicketEvent, e: TicketEvent): boolean {
    if (currentGroup == null) { return false; }
    if (currentGroup.user.id !== e.user.id) { return false; }

    let startTime = currentGroup.events[0].time;
    let timeFrame = 15 * 1000 * 60; // 15 min
    let newTime = e.time;
    return newTime < (startTime + timeFrame);
  }

}

export class GroupedTicketEvent {
  user: TicketDetailUser;
  events: TicketEvent[];

  constructor(user: TicketDetailUser) {
    this.user = user;
    this.events = [];
  }
}
