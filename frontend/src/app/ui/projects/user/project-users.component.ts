import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ApiCallService, AuthService, User } from '../../../service';
import { ProjectApi, MemberApi, ProjectUserResultJson, ProjectRoleResultJson } from '../../../api';
import { AssignmentTagResultJson } from '../../../api/model/AssignmentTagResultJson';
import { AssignmenttagApi } from '../../../api/api/AssignmenttagApi';
@Component({
  selector: 'tt-project-users',
  templateUrl: './project-users.component.html',
  styleUrls: ['./project-users.component.scss']
})
export class ProjectUsersComponent implements OnInit {
  users: ProjectUserResultJson[];

  iconsCss = {
    sortAscending: 'glyphicon glyphicon-chevron-down',
    sortDescending: 'glyphicon glyphicon-chevron-up',
    pagerLeftArrow: 'glyphicon glyphicon-chevron-left',
    pagerRightArrow: 'glyphicon glyphicon-chevron-right',
    pagerPrevious: 'glyphicon glyphicon-backward',
    pagerNext: 'glyphicon glyphicon-forward'
  };
  loading = true;
  refresh = true;
  disabled = false;
  projectId: string;
  rows: ProjectUserResultJson[];
  temp: ProjectUserResultJson[];
  totalElements = 0;
  filter: string = '';
  private user: User;
  private roles: ProjectRoleResultJson[];
  private tags: AssignmentTagResultJson[];
  private filterRole = '';
  private cu = false;
  private mode = '';
  private toUpdateMember: ProjectUserResultJson = undefined;

  constructor(private route: ActivatedRoute,
    private router: Router,
    private projectApi: ProjectApi,
    private memberApi: MemberApi,
    private apiCallService: ApiCallService,
    private authService: AuthService,
    private assignmentTagApi: AssignmenttagApi) {
  }

  ngOnInit(): void {
    this.getRoles();
    this.route.params
      .do(() => {
      })
      .switchMap(params => {
        let projectId = params['projectId'];
        this.projectId = projectId;
        return projectId;
      }, error => {
      })
      .subscribe(result => {
      });
    this.users = [];
    this.getProjectMembers();
    this.getAssignmentTags();
    this.user = this.authService.user;
    this.authService.observeUser()
      .subscribe(user => {
        this.user = user;
      });
  }

  getProjectMembers(): void {
    this.apiCallService
      .callNoError<ProjectUserResultJson[]>(h => this.projectApi.listProjectMembersUsingGETWithHttpInfo(this.projectId, this.disabled, h))
      .subscribe(users => {
        this.refresh = true;
        this.users = users;
        this.rows = this.users.filter(e => (e.name.toLocaleLowerCase().indexOf(this.filter) >= 0 ||
          e.username.toLocaleLowerCase().indexOf(this.filter) >= 0 ||
          e.mail.toLocaleLowerCase().indexOf(this.filter) >= 0) &&
          (e.projectRole.toString().indexOf(this.filterRole) >= 0));
        this.loading = false;
        this.refresh = false;
      });
  }

  updateFilter(event?: any) {
    if (event) {
      this.filter = event.target.value.toLocaleLowerCase();
    }
    // filter our data
    if (this.filterRole === 'NONE' || this.filterRole !== 'NONE' && this.disabled) {
      this.disabled = this.filterRole === 'NONE';
      this.refresh = true;
      this.getProjectMembers();
    } else {
      this.rows = this.users.filter(e => (e.name.toLocaleLowerCase().indexOf(this.filter) >= 0 ||
        e.username.toLocaleLowerCase().indexOf(this.filter) >= 0 ||
        e.mail.toLocaleLowerCase().indexOf(this.filter) >= 0) &&
        (e.projectRole.toString().indexOf(this.filterRole) >= 0));
    }
  }

  getRoles(): void {
    this.apiCallService
      .callNoError<ProjectRoleResultJson[]>(h => this.projectApi.listProjectRolesUsingGETWithHttpInfo(h))
      .subscribe(roles => {
        this.roles = roles;
      });
  }

  getAssignmentTags(): void {
    this.apiCallService
      .callNoError<AssignmentTagResultJson[]>(h => this.assignmentTagApi.listAssignmentTagsUsingGETWithHttpInfo(this.projectId, h))
      .subscribe(tags => {
        this.tags = tags;
      });
  }

  onDisable(userId: string) {
    this.apiCallService
      .callNoError<void>(h => this.memberApi.deleteMemberUsingDELETEWithHttpInfo(userId, this.projectId, h))
      .subscribe(params => {
        this.users.filter(u => u.id === userId)[0].projectRole = ProjectUserResultJson.ProjectRoleEnum.NONE;
        this.updateFilter();
      }, error => {
      });
  }

  getAssgimentTagForID(id: string): AssignmentTagResultJson {
    for (let tag of this.tags) {
      if (tag.id === id) {
        return tag;
      }
    }
    return null;
  }

  onStartAdd() {
    this.mode = 'Add';
    this.cu = true;
  }

  onStartUpdate(member: ProjectUserResultJson) {
    this.toUpdateMember = member;
    this.cu = true;
    this.mode = 'Update';
  }

  onStopCreate() {
    this.cu = false;
  }

  finishCreateUpdate() {
    this.cu = false;
    this.getProjectMembers();
  }


}
