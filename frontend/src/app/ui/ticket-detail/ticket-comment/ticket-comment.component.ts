import { Component, Input, OnChanges, ElementRef } from '@angular/core';
import { TicketDetailComment, TicketDetailAssTag } from '../ticket-detail';
import * as imm from 'immutable';
import { Subscription } from 'rxjs';
import { ImagesService } from '../../../service';
import { UserApi } from '../../../api';

@Component({
  selector: 'tt-ticket-comment',
  templateUrl: './ticket-comment.component.html',
  styleUrls: ['./ticket-comment.component.scss']
})
export class TicketCommentComponent implements OnChanges {
  @Input() comment: TicketDetailComment;
  @Input() userTags: imm.List<TicketDetailAssTag>;

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
        'user-image-' + this.comment.user.id,
        p => this.userApi.getUserImageUsingGETWithHttpInfo(this.comment.user.id, p))
      .subscribe(data => {
        const element = (this.element.nativeElement as HTMLElement).querySelector('.user-image') as HTMLImageElement;
        element.src = data;
        this.imageSubscription = null;
      });
  }
}
