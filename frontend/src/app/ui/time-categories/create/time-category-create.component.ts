import {Component, Output, EventEmitter, Input} from '@angular/core';
import {ApiCallService, ApiCallResult} from '../../../service';
import {showValidationError} from '../../../util/error';
import {Modal} from 'angular2-modal/plugins/bootstrap';
import {CreateTimeCategoryRequestJson} from '../../../api/model/CreateTimeCategoryRequestJson';
import {TimeCategoryJson} from '../../../api/model/TimeCategoryJson';
import {TimecategoryApi} from '../../../api/api/TimecategoryApi';

@Component({
  selector: 'tt-time-category-create',
  templateUrl: './time-category-create.component.html',
  styleUrls: ['./time-category-create.component.scss']
})

export class TimeCategoryCreateComponent {

  request: CreateTimeCategoryRequestJson = {
    projectId: '',
    name: ''
  };
  working = false;
  @Input() projectId: string;
  @Output() readonly created = new EventEmitter<CreateTimeCategoryRequestJson>();

  constructor(
    private apiCallService: ApiCallService,
    private timeCategoryApi: TimecategoryApi,
    private modal: Modal) { }

  onSubmit(): void {
    this.working = true;
    this.request.projectId = this.projectId;
    this.apiCallService
      .call<TimeCategoryJson>(h => this.timeCategoryApi.createTimeCategoryUsingPOSTWithHttpInfo(this.request, h))
      .subscribe(
      result => {
        if (result.isValid) {
          this.request.name = '';
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
