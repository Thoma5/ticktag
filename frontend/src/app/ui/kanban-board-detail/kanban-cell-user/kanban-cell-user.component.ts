import {Component, Input, ElementRef, OnChanges} from '@angular/core';
import {KanbanDetailUser} from '../kanban-board-detail.component';
import {UserApi} from '../../../api/api/UserApi';
import {ImagesService} from '../../../service/images/images.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'tt-kanban-cell-user',
  templateUrl: './kanban-cell-user.component.html',
  styleUrls: ['./kanban-cell-user.component.scss']
})
export class KanbanCellUserComponent implements OnChanges {
  @Input() user: KanbanDetailUser;

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
      'user-image-' + this.user.id,
      p => this.userApi.getUserImageUsingGETWithHttpInfo(this.user.id, p))
      .subscribe(data => {
        const element = (this.element.nativeElement as HTMLElement).querySelector('.user-image') as HTMLImageElement;
        element.src = data;
        this.imageSubscription = null;
      });
  }
}


