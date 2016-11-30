import { Component, Input } from '@angular/core';

@Component({
  selector: 'tt-ticket-event-old-new-markup',
  templateUrl: './ticket-event-old-new-markup.component.html',
  styleUrls: ['../ticket-events.component.scss']
})
export class TicketEventOldNewMarkupComponent {
  @Input() title: String;
  @Input() oldString: String;
  @Input() newString: String;

  isOldCollapsed: boolean = true;
  isNewCollapsed: boolean = true;

  onCollapseOld() {
    this.isOldCollapsed = ! this.isOldCollapsed;
  }

  onCollapseNew() {
    this.isNewCollapsed = ! this.isNewCollapsed;
  }

  contentTitle(boolHidden: Boolean) {
    if (boolHidden) {
      return 'Show content';
    } else {
      return 'Hide content';
    }
  }
}
