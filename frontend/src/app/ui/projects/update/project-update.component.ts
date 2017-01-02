import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { ApiCallService, ApiCallResult } from '../../../service';
import { ProjectApi, UpdateProjectRequestJson, ProjectResultJson } from '../../../api';
import { showValidationError } from '../../../util/error';
import { Modal } from 'angular2-modal/plugins/bootstrap';

@Component({
  selector: 'tt-project-update',
  templateUrl: './project-update.component.html',
  styleUrls: ['./project-update.component.scss']
})
export class ProjectUpdateComponent implements OnInit {
  request: UpdateProjectRequestJson = {
    name: undefined,
    description: undefined,
    icon: undefined,
    disabled: undefined
  };
  active: Boolean;
  upload = false;
  working = false;
  iconWithMimeType = '';
  private defaultIcon: string;
  @Input() project: ProjectResultJson;
  @Output() readonly updated = new EventEmitter<ProjectResultJson>();

  constructor(private apiCallService: ApiCallService,
    private projectApi: ProjectApi,
    private modal: Modal) { }


  ngOnInit(): void {
    this.defaultIcon = this.project.icon ? this.project.icon.split(',')[1] : undefined;
    this.revert();
  }
  onSubmit(): void {
    this.working = true;
    this.apiCallService
      .call<ProjectResultJson>(h => this.projectApi.updateProjectUsingPUTWithHttpInfo(this.project.id, this.request, h))
      .subscribe(
      result => {
        if (result.isValid) {
          this.request.name = '';
          this.request.description = '';
          this.request.icon = '';
          this.updated.emit(result.result);
        } else {
          this.error(result);
        }
      },
      undefined,
      () => { this.working = false; });
  }

  setImage(img: string) {
    this.iconWithMimeType = img;
    if (img && img !== this.defaultIcon) {
      this.request.icon = img.split(',')[1];
    } else if (img && img === this.defaultIcon) {
      this.request.icon = undefined;
    } else {
      this.request.icon = '';
    }
  }
  revert() {
    this.request.name = this.project.name;
    this.request.description = this.project.description;
    this.request.icon = this.defaultIcon;
    this.iconWithMimeType = this.project.icon;
    this.request.disabled = this.project.disabled;
    this.active = !this.project.disabled;
  }

  changeActive() {
    this.request.disabled = !this.active;

  }

  private error(result: ApiCallResult<void|{}>): void {
    showValidationError(this.modal, result);
  }
}
