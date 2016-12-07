import { Component, Input, OnChanges, ElementRef } from '@angular/core';
import { TicketEvent } from '../../ticket-detail';
import { ImagesService } from '../../../../service';
import { UserApi } from '../../../../api';
import { Subscription } from 'rxjs';

@Component({
  selector: 'tt-ticket-event',
  templateUrl: './ticket-event.component.html',
  styleUrls: ['../ticket-events.component.scss']
})
export class TicketEventComponent implements OnChanges {
  @Input() event: TicketEvent;

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
        'user-image-' + this.event.user.id,
        p => this.userApi.getUserImageUsingGETWithHttpInfo(this.event.user.id, p))
      .subscribe(data => {
        const element = (this.element.nativeElement as HTMLElement).querySelector('.user-image') as HTMLImageElement;
        element.src = data;
        this.imageSubscription = null;
      });
  }
}
