import { Component, Output, EventEmitter, ViewContainerRef } from '@angular/core';
import { ApiCallService, ApiCallResult } from '../../../service';
import { ProjectApi, CreateProjectRequestJson, ProjectResultJson } from '../../../api';
import { showValidationError } from '../../../util/error';
import { Modal } from 'angular2-modal/plugins/bootstrap';
import { Overlay } from 'angular2-modal';

@Component({
  selector: 'tt-project-create',
  templateUrl: './project-create.component.html',
  styleUrls: ['./project-create.component.scss']
})
export class ProjectCreateComponent {
  request: CreateProjectRequestJson = {
    name: '',
    description: '',
    icon: undefined
  };
  iconWithMimeType: String = undefined;
  upload = false;
  working = false;
  @Output() readonly created = new EventEmitter<ProjectResultJson>();

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(
    private apiCallService: ApiCallService,
    private projectApi: ProjectApi,
    private modal: Modal,
    private overlay: Overlay,
    private vcRef: ViewContainerRef,
  ) {
    overlay.defaultViewContainer = vcRef;
  }

  onSubmit(): void {
    this.working = true;
    this.apiCallService
      .call<ProjectResultJson>(h => this.projectApi.createProjectUsingPOSTWithHttpInfo(this.request, h))
      .subscribe(
      result => {
        if (result.isValid) {
          this.request.name = '';
          this.request.description = '';
          this.request.icon = undefined;
          this.created.emit(result.result);
        } else {
          this.error(result);
        }
      },
      undefined,
      () => { this.working = false; });
  }

  setImage(image: string) {
    this.iconWithMimeType = image;
    this.request.icon = image ? image.split(',')[1] : undefined;
  }

  private error(result: ApiCallResult<void | {}>): void {
    showValidationError(this.modal, result);
  }
}
