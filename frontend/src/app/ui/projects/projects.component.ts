import {Component, OnInit} from '@angular/core';
import {ApiCallService} from '../../service';
import {ProjectApi, ProjectResultJson} from '../../api';

@Component({
  selector: 'tt-projects',
  templateUrl: './projects.component.html',
})
export class ProjectsComponent implements OnInit {
  projects: ProjectResultJson[];

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(private projectApi: ProjectApi,
              private apiCallService: ApiCallService) {
  }

  ngOnInit(): void {
    this.getProjects();
  }

  getProjects(page?: number, size?: number, order?: string, asc?: boolean, name?: string): void {
    this.apiCallService
      .callNoError<ProjectResultJson[]>(h => this.projectApi.listProjectsUsingGETWithHttpInfo(page, size , order, asc, name, false, h))
      .subscribe(projects => { this.projects = projects; });
  }
}
