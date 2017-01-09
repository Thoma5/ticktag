import { Component, Input } from '@angular/core';
import { User } from '../../service';
import { ProjectResultJson } from '../../api';
import * as _ from 'lodash';
import * as imm from 'immutable';

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
  @Input()
  projects: imm.List<ProjectResultJson>;

  showingSelection: boolean = false;

  get isAdmin(): boolean {
    return _.includes(this.user.authorities, 'ADMIN');
  }

  get otherProjects(): imm.List<ProjectResultJson> {
    if (this.project == null) {
      return this.projects;
    } else {
      return this.projects.filter(p => p.id !== this.project.id).toList();
    }
  }

  onProjectClick() {
    this.showingSelection = !this.showingSelection;
  }
}
