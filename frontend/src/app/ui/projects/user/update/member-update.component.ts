import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { ApiCallService } from '../../../../service';
import {
  ProjectApi, MemberApi, UpdateMemberRequestJson,
  ProjectUserResultJson, MemberResultJson
} from '../../../../api';
import { RoleResultJson } from '../../../../api/model/RoleResultJson';

@Component({
  selector: 'tt-member-update',
  templateUrl: './member-update.component.html',
  styleUrls: ['./member-update.component.scss']
})

export class MemberUpdateComponent implements OnInit {
  request: UpdateMemberRequestJson = {
    projectRole: undefined,
  };
  working = false;
  @Input() readonly user: ProjectUserResultJson;
  @Input() readonly roles: RoleResultJson[];
  @Output() readonly updated = new EventEmitter<MemberResultJson>();

  constructor(private apiCallService: ApiCallService,
    private projectApi: ProjectApi,
    private memberApi: MemberApi) { }

  ngOnInit(): void {
    this.request.projectRole = this.user.projectRole;
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
          window.alert('Could not update membership:\n\n' + JSON.stringify(result.error));
        }
      },
      undefined,
      () => { this.working = false; });
  }
  revert() {
    this.request.projectRole = this.user.projectRole;
  }
}
