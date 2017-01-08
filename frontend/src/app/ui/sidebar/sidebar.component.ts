import { Component, Input } from '@angular/core';
import { User } from '../../service';
import { ProjectResultJson } from '../../api';
import * as _ from 'lodash';

@Component({
  selector: 'tt-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  @Input()
  user: User;
  @Input()
  project: ProjectResultJson | null;
  @Input()
  loadingProject: boolean;

  get isAdmin(): boolean {
    return _.includes(this.user.authorities, 'ADMIN');
  }
}
