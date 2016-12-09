import { Component, Input, EventEmitter, Output, OnChanges, ElementRef } from '@angular/core';
import { TicketDetailAssTag, TicketDetailUser } from '../ticket-detail';
import { ImagesService } from '../../../service';
import { UserApi } from '../../../api';
import { Subscription } from 'rxjs';
import * as imm from 'immutable';

@Component({
  selector: 'tt-assigned-user',
  templateUrl: './assigned-user.component.html',
  styleUrls: ['./assigned-user.component.scss']
})
export class AssignedUserComponent implements OnChanges {
  @Input() user: TicketDetailUser;
  @Input() tags: imm.List<{ id: string, transient: boolean }>;
  @Input() allTags: imm.Map<string, TicketDetailAssTag>;

  @Output() readonly tagAdd = new EventEmitter<string>();
  @Output() readonly tagRemove = new EventEmitter<string>();

  private imageSubscription: Subscription;

  constructor(
    private element: ElementRef,
    private images: ImagesService,
    private userApi: UserApi) {}

  ngOnChanges(): void {
    if (this.imageSubscription) {
      this.imageSubscription.unsubscribe();
    }
    this.imageSubscription = this.images.loadImage(
        'user-image-' + this.user.id,
        p => this.userApi.getUserImageUsingGETWithHttpInfo(this.user.id, p))
      .subscribe(data => {
        const element = (this.element.nativeElement as HTMLElement).querySelector('.user-image') as HTMLImageElement;
        element.src = data;
        this.imageSubscription = null;
      });
  }
}
