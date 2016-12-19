import { Component, Input } from '@angular/core';
import { UserApi } from '../../api';

@Component({
  selector: 'tt-user-image',
  templateUrl: 'user-image.component.html',
  styleUrls: ['./user-image.component.scss'],
})
export class UserImageComponent {
  @Input() imageId: string;
  readonly imagePath: string = '';

  constructor(private userApi: UserApi) {
    // This is a terrible, terrible hack to bypass the visibility modifier
    // But I don't know how else to get the base path
    this.imagePath = (<any>userApi).basePath + '/user/image';
  }
}
