import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {ApiCallService, ApiCallResult} from '../../../service';
import {showValidationError} from '../../../util/error';
import {Modal} from 'angular2-modal/plugins/bootstrap';
import {AssignmentTagResultJson} from '../../../api/model/AssignmentTagResultJson';
import {UpdateTimeCategoryRequestJson} from '../../../api/model/UpdateTimeCategoryRequestJson';
import {TimeCategoryJson} from '../../../api/model/TimeCategoryJson';
import {TimecategoryApi} from '../../../api/api/TimecategoryApi';


@Component({
  selector: 'tt-time-category-update',
  templateUrl: './time-category-update.component.html',
  styleUrls: ['./time-category-update.component.scss']
})

export class TimeCategoryUpdateComponent implements OnInit {
  request: UpdateTimeCategoryRequestJson = {
    name: undefined
  };
  active: Boolean;
  working = false;
  @Input() timeCategory: TimeCategoryJson;
  @Output() readonly updated = new EventEmitter<TimeCategoryJson>();

  constructor(
    private apiCallService: ApiCallService,
    private timeCategoryApi: TimecategoryApi,
    private modal: Modal) { }

  ngOnInit(): void {
    this.revert();
  }

  onSubmit(): void {
    this.working = true;
    this.apiCallService
      .call<AssignmentTagResultJson>(h => this.timeCategoryApi
        .updateTimeCategoryUsingPUTWithHttpInfo(this.timeCategory.id, this.request, h))
      .subscribe(
      result => {
        if (result.isValid) {
          this.request.name = '';
          this.updated.emit(result.result);
        } else {
          this.error(result);
        }
      },
      undefined,
      () => { this.working = false; });
  }

  revert() {
    this.request.name = this.timeCategory.name;
  }

  private error(result: ApiCallResult<void | {}>): void {
    showValidationError(this.modal, result);
  }
}
