import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ApiCallService, AuthService, User } from '../../service';
import { ProjectApi, PageProjectResultJson, ProjectResultJson } from '../../api';
@Component({
  selector: 'tt-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./project.component.scss']
})
export class ProjectsComponent implements OnInit {
  projects: PageProjectResultJson;

  iconsCss = {
    sortAscending: 'glyphicon glyphicon-chevron-down',
    sortDescending: 'glyphicon glyphicon-chevron-up',
    pagerLeftArrow: 'glyphicon glyphicon-chevron-left',
    pagerRightArrow: 'glyphicon glyphicon-chevron-right',
    pagerPrevious: 'glyphicon glyphicon-backward',
    pagerNext: 'glyphicon glyphicon-forward'
  };
  loading = true;
  refresh = true;
  cu = false;
  mode = '';
  toUpdateProject: PageProjectResultJson = undefined;
  asc = true;
  sortprop = 'NAME';
  offset = 0;
  limit = 25;
  rows: ProjectResultJson[] = [];
  totalElements = 0;
  filter: string = '';
  private allProjects: boolean = false;
  private user: User;



  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(
    private router: Router,
    private projectApi: ProjectApi,
    private apiCallService: ApiCallService,
    private authService: AuthService) {
  }

  ngOnInit(): void {
    this.projects = [];
    this.getProjects(this.offset, this.limit, 'NAME', true, undefined);
    this.user = this.authService.user;
    this.authService.observeUser()
      .subscribe(user => {
        this.user = user;
      });
  }

  getProjects(page?: number, size?: number, order?: string, asc?: boolean, name?: string): void {
    this.apiCallService
      .callNoError<PageProjectResultJson>(h => this.projectApi
      .listProjectsUsingGETWithHttpInfo(page, size, order, asc, name, this.allProjects, h))
      .subscribe(projects => {
        this.refresh = true;
        this.projects = projects;
        this.totalElements = projects.totalElements;
        const start = this.offset * this.limit;
        const end = start + this.limit;
        let rows = [...this.rows];
        for (let i = start; i < end; i++) {
          rows[i] = projects.content[i - this.offset * this.limit];
        }
        this.rows = rows;
        this.loading = false;
        this.refresh = false;
      });
  }

  onPage(event: any) {
    this.refresh = true;
    this.limit = event.limit;
    this.offset = event.offset;
    this.getProjects(event.offset, event.limit, this.sortprop, this.asc, undefined);

  }

  onSort(event: any) {
    this.refresh = true;
    this.asc = event.sorts[0].dir === 'asc' ? true : false;
    this.sortprop = event.sorts[0].prop === 'name' ? 'NAME' : 'CREATION_DATE';
    this.getProjects(this.offset, this.limit, this.sortprop, this.asc, undefined);
  }

  updateFilter(event: any) {
    this.filter = event.target.value;

    // filter our data
    this.offset = 0;
    this.getProjects(this.offset, this.limit, this.sortprop, this.asc, this.filter);
  }

  activate(event: any) {
    if (event.type === 'keydown' && event.event.code === 'Enter') {
      this.router.navigate(['/project', event.row.id, 'tickets']);
    }
  }

  onDeleteClicked(id: string) {
    this.apiCallService
      .call<ProjectResultJson>(h => this.projectApi.deleteProjectUsingDELETEWithHttpInfo(id, h))
      .subscribe( param => {
        this.refresh = true;
        this.getProjects(this.offset, this.limit, this.sortprop, this.asc, undefined);
      }
      );
  }
  onStartCreate() {
    this.mode = 'Create';
    this.cu = true;
  }
  onStartUpdate(project: ProjectResultJson) {
    this.toUpdateProject = project;
    this.cu = true;
    this.mode = 'Update';
  }
  onStopCreate() {
    this.cu = false;
  }
  cuFinished() {
    this.cu = false;
    this.getProjects();
  }
}
