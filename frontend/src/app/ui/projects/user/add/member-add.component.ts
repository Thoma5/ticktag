import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ApiCallService } from '../../../../service';
import {
  UserApi, ProjectApi, MemberApi, CreateMemberRequestJson, UserResultJson, MemberResultJson
} from '../../../../api';
import { RoleResultJson } from '../../../../api/model/RoleResultJson';
import RoleEnum = CreateMemberRequestJson.ProjectRoleEnum;

@Component({
  selector: 'tt-member-add',
  templateUrl: './member-add.component.html',
  styleUrls: ['./member-add.component.scss']
})

export class MemberAddComponent implements OnInit {
  request: CreateMemberRequestJson = {
    projectRole: RoleEnum.USER,
  };
  private loading = true;
  private selectedUser: UserResultJson;
  working = false;
  confPassword: string = undefined;
  private roles: RoleResultJson[] = [];
  @Input() projectId: string;
  @Input() assignedUsers: UserResultJson[];
  @Output() readonly created = new EventEmitter<MemberResultJson>();

  constructor(private apiCallService: ApiCallService,
    private route: ActivatedRoute,
    private router: Router,
    private projectApi: ProjectApi,
    private userApi: UserApi,
    private memberApi: MemberApi) { }

  ngOnInit(): void {
    if (!this.projectId) {
      this.route.params
        .do(() => { this.loading = true; })
        .switchMap(params => {
          let projectId = params['projectId'];
          this.projectId = projectId;
          return projectId;
        }, error => { })
        .subscribe(result => {
          this.loading = false;
        });
      this.getProjectMembers();
    }
    this.getRoles();
    this.loading = false;
  }
  getProjectMembers(): void {
    this.apiCallService
      .callNoError<UserResultJson[]>(h => this.projectApi.listProjectMembersUsingGETWithHttpInfo(this.projectId, undefined, h))
      .subscribe(users => {
        this.assignedUsers = users;
      });
  }

  getRoles(): void {
    this.apiCallService
      .callNoError<RoleResultJson[]>(h => this.projectApi.listProjectRolesUsingGETWithHttpInfo(h))
      .subscribe(roles => {
        this.roles = roles;
      });
  }
  onSubmit(): void {
    this.working = true;
    this.apiCallService
      .call<MemberResultJson>(h => this.memberApi.createMemberUsingPOSTWithHttpInfo(this.selectedUser.id, this.projectId, this.request, h))
      .subscribe(
      result => {
        if (result.isValid) {
          this.selectedUser = undefined;
          this.created.emit(result.result);
        } else {
          window.alert('Could not assign user:\n\n' + JSON.stringify(result.error));
        }
      },
      undefined,
      () => { this.working = false; });
  }
  onAssignUser(user: UserResultJson) {
    this.selectedUser = user;
  }
}
