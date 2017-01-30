import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {ApiCallService, ApiCallResult} from '../../../service';
import {showValidationError} from '../../../util/error';
import {Modal} from 'angular2-modal/plugins/bootstrap';
import {UpdateAssignmentRequestJson} from '../../../api/model/UpdateAssignmentRequestJson';
import {AssignmentTagResultJson} from '../../../api/model/AssignmentTagResultJson';
import {AssignmenttagApi} from '../../../api/api/AssignmenttagApi';


@Component({
  selector: 'tt-assignment-tag-update',
  templateUrl: './assignment-tag-update.component.html',
  styleUrls: ['./assignment-tag-update.component.scss']
})

export class AssignmentTagUpdateComponent implements OnInit {
  request: UpdateAssignmentRequestJson = {
    name: undefined,
    color: undefined
  };
  active: Boolean;
  working = false;
  @Input() assignmentTag: AssignmentTagResultJson;
  @Output() readonly updated = new EventEmitter<AssignmentTagResultJson>();

  constructor(
    private apiCallService: ApiCallService,
    private assignmentTagApi: AssignmenttagApi,
    private modal: Modal) { }

  ngOnInit(): void {
    this.revert();
  }

  onSubmit(): void {
    this.working = true;
    this.request.color = this.request.color.substring(1);
    this.apiCallService
      .call<AssignmentTagResultJson>(h => this.assignmentTagApi
        .updateAssignmentTagUsingPUTWithHttpInfo(this.assignmentTag.id, this.request, h))
      .subscribe(
      result => {
        if (result.isValid) {
          this.request.name = '';
          this.request.color = '';
          this.updated.emit(result.result);
        } else {
          this.error(result);
        }
      },
      undefined,
      () => { this.working = false; });
  }

  revert() {
    this.request.name = this.assignmentTag.name;
    this.request.color = '#' + this.assignmentTag.color;
  }

  private error(result: ApiCallResult<void | {}>): void {
    showValidationError(this.modal, result);
  }
}
