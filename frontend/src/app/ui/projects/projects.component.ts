import { Component, OnInit } from '@angular/core';
import { ApiCallService } from '../../service';
import { ProjectApi, PageProjectResultJson } from '../../api';

@Component({
  selector: 'tt-projects',
  templateUrl: './projects.component.html',
})
export class ProjectsComponent implements OnInit {
  private projects: PageProjectResultJson;
  columns = [
    { name: 'ID' },
    { name: 'Name' },
    { name: 'Description' },
    { name: 'CreationDate' },
    { name: 'Icon' }
  ];
  private loading = true;
  private offset = 0;
  private limit = 10;

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(private projectApi: ProjectApi,
    private apiCallService: ApiCallService) {
  }

  ngOnInit(): void {
    this.projects = [];
    this.getProjects(this.offset, this.limit, 'NAME', true, undefined);
  }

  getProjects(page?: number, size?: number, order?: string, asc?: boolean, name?: string): void {
    this.apiCallService
      .callNoError<PageProjectResultJson>(h => this.projectApi.listProjectsUsingGETWithHttpInfo(page, size, order, asc, name, true, h))
      .subscribe(projects => {
        this.projects = projects;
        this.loading = false;
      });
  }
  onPage() {
    this.loading = true;
    this.getProjects(++this.offset, this.limit, 'NAME', true, undefined);
  }

}
