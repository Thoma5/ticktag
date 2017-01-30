import { Component, Input, Output, EventEmitter, OnInit, ViewContainerRef } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ApiCallService, ApiCallResult } from '../../../../service';
import {
  UserApi, ProjectApi, MemberApi, AssignmenttagApi, CreateMemberRequestJson, UserResultJson, MemberResultJson, AssignmentTagResultJson
} from '../../../../api';
import { RoleResultJson } from '../../../../api/model/RoleResultJson';
import { showValidationError } from '../../../../util/error';
import { Modal } from 'angular2-modal/plugins/bootstrap';
import { Overlay } from 'angular2-modal';
import RoleEnum = CreateMemberRequestJson.ProjectRoleEnum;

@Component({
  selector: 'tt-member-add',
  templateUrl: './member-add.component.html',
  styleUrls: ['./member-add.component.scss']
})

export class MemberAddComponent implements OnInit {
  request: CreateMemberRequestJson = {
    projectRole: RoleEnum.USER,
    defaultAssignmentTagId: undefined
  };
  private loading = true;
  private selectedUser: UserResultJson;
  working = false;
  confPassword: string = undefined;
  private roles: RoleResultJson[] = [];
  private assignmentTags: AssignmentTagResultJson[];
  @Input() projectId: string;
  @Input() assignedUsers: UserResultJson[];

  @Output() readonly created = new EventEmitter<MemberResultJson>();

  constructor(private apiCallService: ApiCallService,
    private route: ActivatedRoute,
    private router: Router,
    private projectApi: ProjectApi,
    private userApi: UserApi,
    private memberApi: MemberApi,
    private assignmentTagApi: AssignmenttagApi,
    private modal: Modal,
    private overlay: Overlay,
    private vcRef: ViewContainerRef,
  ) {
    overlay.defaultViewContainer = vcRef;
  }

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
    this.getAssignmentTags();
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

  getAssignmentTags(): void {
    this.apiCallService
      .callNoError<AssignmentTagResultJson[]>(h => this.assignmentTagApi.listAssignmentTagsUsingGETWithHttpInfo(this.projectId, h))
      .subscribe(tags => {
        this.assignmentTags = tags;
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
          this.getProjectMembers();
          this.selectedUser = undefined;
          this.created.emit(result.result);
        } else {
          this.error(result);
        }
      },
      undefined,
      () => { this.working = false; });
  }

  onAssignUser(user: UserResultJson) {
    this.selectedUser = user;
  }

  onDefaultAssTagSelect(event: any) {
    this.request.defaultAssignmentTagId = this.request.defaultAssignmentTagId === '' ? undefined : this.request.defaultAssignmentTagId;
  }

  private error(result: ApiCallResult<void | {}>): void {
    showValidationError(this.modal, result);
  }
}
