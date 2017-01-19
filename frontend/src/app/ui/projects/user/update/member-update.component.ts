import { Component, Input, Output, EventEmitter, OnInit, ViewContainerRef } from '@angular/core';
import { ApiCallService, ApiCallResult } from '../../../../service';
import {
  ProjectApi, MemberApi, UpdateMemberRequestJson,
  ProjectUserResultJson, MemberResultJson
} from '../../../../api';
import { showValidationError } from '../../../../util/error';
import { Modal } from 'angular2-modal/plugins/bootstrap';
import { Overlay } from 'angular2-modal';
import { RoleResultJson } from '../../../../api/model/RoleResultJson';
import {AssignmentTagResultJson} from "../../../../api/model/AssignmentTagResultJson";

@Component({
  selector: 'tt-member-update',
  templateUrl: './member-update.component.html',
  styleUrls: ['./member-update.component.scss']
})

export class MemberUpdateComponent implements OnInit {
  request: UpdateMemberRequestJson = {
    projectRole: undefined,
    defaultAssignmentTagId: null
  };
  working = false;
  @Input() readonly user: ProjectUserResultJson;
  @Input() readonly roles: RoleResultJson[];
  @Input() readonly assignmentTags: AssignmentTagResultJson[];
  @Output() readonly updated = new EventEmitter<MemberResultJson>();

  constructor(private apiCallService: ApiCallService,
    private projectApi: ProjectApi,
    private memberApi: MemberApi,
    private modal: Modal,
    private overlay: Overlay,
    private vcRef: ViewContainerRef,
  ) {
    overlay.defaultViewContainer = vcRef;
  }

  ngOnInit(): void {
    this.request.projectRole = this.user.projectRole;
    this.request.defaultAssignmentTagId = this.user.defaultAssignmentTagId
  }

  onSubmit(): void {
    this.working = true;
    this.apiCallService
      .call<MemberResultJson>(h => this.memberApi.updateMemberUsingPUTWithHttpInfo(this.user.id, this.user.projectId, this.request, h))
      .subscribe(
      result => {
        if (result.isValid) {
          this.request.projectRole = undefined;
          this.updated.emit(result.result);
        } else {
          this.error(result);
        }
      },
      undefined,
      () => { this.working = false; });
  }

  revert() {
    this.request.projectRole = this.user.projectRole;
    this.request.defaultAssignmentTagId = this.user.defaultAssignmentTagId;
  }

  private error(result: ApiCallResult<void | {}>): void {
    showValidationError(this.modal, result);
  }
}
