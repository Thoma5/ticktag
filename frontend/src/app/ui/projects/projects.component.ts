import { Component, OnInit } from '@angular/core';
import { ApiCallService } from '../../service';
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
  asc = true;
  sortprop = 'NAME';
  offset = 0;
  limit = 15;
  rows: ProjectResultJson[] = [];
  totalElements = 0;


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

  onPage(event) {
    this.refresh = true;
    console.log('Page Event', event);
    this.limit = event.limit;
    this.offset = event.offset;
    this.getProjects(event.offset, event.limit, this.sortprop, this.asc, undefined);

  }

  onSort(event) {
    this.refresh = true;
    console.log('Sort Event', event);
    this.asc = event.sorts[0].dir === 'asc' ? true : false;
    this.sortprop = event.sorts[0].prop === 'name' ? 'NAME' : 'CREATION_DATE';
    this.getProjects(this.offset, this.limit, this.sortprop, this.asc, undefined);
  }

  updateFilter(event) {
    let val = event.target.value;

    // filter our data
    this.offset = 0;
    this.getProjects(this.offset, this.limit, this.sortprop, this.asc, val);
  }




}
