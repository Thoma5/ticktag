import { Component, Input } from '@angular/core';
import { User } from '../../service';
import { ProjectResultJson, ProjectUserResultJson } from '../../api';
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
  projectRole: ProjectUserResultJson.ProjectRoleEnum | null;
  @Input()
  loadingProject: boolean;
  @Input()
  projects: imm.List<ProjectResultJson>;

  private _showingSelection = false;

  get showingSelection(): boolean {
    return this._showingSelection && !this.loadingProject;
  }

  get isAdmin(): boolean {
    return _.includes(this.user.authorities, 'ADMIN');
  }

  get isProjectAdmin(): boolean {
    return this.projectRole === ProjectUserResultJson.ProjectRoleEnum.ADMIN;
  }

  get otherProjects(): imm.List<ProjectResultJson> {
    if (this.project == null) {
      return this.projects;
    } else {
      return this.projects.filter(p => p.id !== this.project.id).toList();
    }
  }

  onProjectClick() {
    this._showingSelection = !this._showingSelection;
  }
}
