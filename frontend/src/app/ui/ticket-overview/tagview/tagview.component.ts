import {
  Component, Input, Output, EventEmitter
} from '@angular/core';
import * as imm from 'immutable';
import {TicketOverviewTag} from '../ticket-overview';


export interface Tag {
  id: string;
  name: string;
  color: string;
  order: number;
  normalizedName: string;
}

export type TagRef =
  string |
  { id: string, transient: boolean };


@Component({
  selector: 'tt-tagview',
  templateUrl: './tagview.component.html',
  styleUrls: ['./tagview.component.scss'],
})
export class TagViewComponent {
  @Input() tags: imm.List<TicketOverviewTag>;
  @Output() clickedTag = new EventEmitter<TicketOverviewTag>();

tagClicked(tag: TicketOverviewTag) {
  this.clickedTag.emit(tag);
}
}
