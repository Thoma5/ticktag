import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { ApiCallService } from '../../../service';
import { ProjectApi, UpdateProjectRequestJson, ProjectResultJson } from '../../../api';

@Component({
  selector: 'tt-project-update',
  templateUrl: './project-update.component.html',
  styleUrls: ['./project-update.component.scss']
})
export class ProjectUpdateComponent implements OnInit {
  request: UpdateProjectRequestJson = {
    name: '',
    description: '',
    icon: undefined,
    disabled: undefined
  };
  active: Boolean;
  upload = false;
  working = false;
  @Input() project: ProjectResultJson;
  @Output() readonly updated = new EventEmitter<ProjectResultJson>();

  constructor(private apiCallService: ApiCallService,
    private projectApi: ProjectApi) { }


  ngOnInit(): void {
    this.request.name = this.project.name;
    this.request.description = this.project.description;
    this.request.icon = this.project.icon;
    this.active = ! this.project.disabled;
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
          window.alert('Could not update project:\n\n' + JSON.stringify(result.error));
        }
      },
      undefined,
      () => { this.working = false; });
  }

  setImage(image: string) {
    this.request.icon = image ? image : null;
  }
  revert() {
    this.request.name = this.project.name;
    this.request.description = this.project.description;
    this.request.icon = this.project.icon;
    this.request.disabled = this.project.disabled;
    this.active = ! this.project.disabled;
  }

  changeActive() {
    this.request.disabled = ! this.active;

  }
}
