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
export class GroupedTicketEventComponent implements OnChanges {
  @Input() group: GroupedTicketEvent;

  private imageSubscription: Subscription;

  constructor(
    private images: ImagesService,
    private userApi: UserApi,
    private element: ElementRef
  ) {}

  ngOnChanges(): void {
    if (this.imageSubscription) {
      this.imageSubscription.unsubscribe();
    }
    this.imageSubscription = this.images.loadImage(
        'user-image-' + this.firstEvent().user.id,
        p => this.userApi.getUserImageUsingGETWithHttpInfo(this.firstEvent().user.id, p))
      .subscribe(data => {
        const element = (this.element.nativeElement as HTMLElement).querySelector('.user-image') as HTMLImageElement;
        element.src = data;
        this.imageSubscription = null;
      });
  }

  firstEvent(): TicketEvent {
    return this.group.events[0];
  }
}
