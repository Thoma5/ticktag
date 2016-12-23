import { Component, Output, EventEmitter } from '@angular/core';
import { ApiCallService } from '../../service';
import { ProjectApi, CreateProjectRequestJson, ProjectResultJson } from '../../api';

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
  upload = false;
  working = false;
  @Output() readonly created = new EventEmitter<ProjectResultJson>();

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(private apiCallService: ApiCallService,
    private projectApi: ProjectApi) { }

  onSubmit(): void {
    this.working = true;
    this.apiCallService
      .call<ProjectResultJson>(h => this.projectApi.createProjectUsingPOSTWithHttpInfo(this.request, h))
      .subscribe(
      result => {
        if (result.isValid) {
          this.request.name = '';
          this.request.description = '';
          this.created.emit(result.result);
        } else {
          window.alert('Could not create project:\n\n' + JSON.stringify(result.error));
        }
      },
      undefined,
      () => { this.working = false; });
  }
}
