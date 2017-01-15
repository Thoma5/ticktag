import {Component, Output, EventEmitter, Input} from '@angular/core';
import {ApiCallService, ApiCallResult} from '../../../service';
import {CreateUserRequestJson} from '../../../api';
import {showValidationError} from '../../../util/error';
import {Modal} from 'angular2-modal/plugins/bootstrap';
import {CreateAssignmentTagRequestJson} from '../../../api/model/CreateAssignmentTagRequestJson';
import {AssignmentTagResultJson} from '../../../api/model/AssignmentTagResultJson';
import {AssignmenttagApi} from '../../../api/api/AssignmenttagApi';
import RoleEnum = CreateUserRequestJson.RoleEnum;

@Component({
  selector: 'tt-assignment-tag-create',
  templateUrl: './assignment-tag-create.component.html',
  styleUrls: ['./assignment-tag-create.component.scss']
})

export class AssignmentTagCreateComponent {

  request: CreateAssignmentTagRequestJson = {
    projectId: '',
    name: '',
    color: ''
  };
  working = false;
  @Input() projectId: string;
  @Output() readonly created = new EventEmitter<AssignmentTagResultJson>();

  constructor(
    private apiCallService: ApiCallService,
    private assignmentTagApi: AssignmenttagApi,
    private modal: Modal) { }

  onSubmit(): void {
    this.working = true;
    this.request.projectId = this.projectId;
    this.request.color = this.request.color.substring(1);
    this.apiCallService
      .call<AssignmentTagResultJson>(h => this.assignmentTagApi.createAssignmentTagUsingPOSTWithHttpInfo(this.request, h))
      .subscribe(
      result => {
        if (result.isValid) {
          this.request.name = '';
          this.request.color = '';
          this.created.emit(result.result);
        } else {
          this.error(result);
        }
      },
      undefined,
      () => { this.working = false; });
  }

  private error(result: ApiCallResult<void | {}>): void {
    showValidationError(this.modal, result);
  }
}
