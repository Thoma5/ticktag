import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ApiCallService, AuthService, User } from '../../../service';
import { ProjectApi, UserResultJson } from '../../../api';
@Component({
  selector: 'tt-project-users',
  templateUrl: './project-users.component.html',
  styleUrls: ['./project-users.component.scss']
})
export class ProjectUsersComponent implements OnInit {
  users: UserResultJson[];

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
  projectId: string;
  rows: UserResultJson[];
  temp: UserResultJson[];
  totalElements = 0;
  filter: string = '';
  private user: User;



  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private projectApi: ProjectApi,
    private apiCallService: ApiCallService,
    private authService: AuthService) {
  }

  ngOnInit(): void {
    this.route.params
      .do(() => { this.loading = true; })
      .switchMap(params => {
        let projectId = params['projectId'];
        this.projectId = projectId;
        return projectId;
      }, error => { })
      .subscribe(result => {
        this.loading = false;
      });
    this.users = [];
    this.getProjects('NAME', true, undefined);
    this.user = this.authService.user;
    this.authService.observeUser()
      .subscribe(user => {
        this.user = user;
      });
  }

  getProjects(order?: string, asc?: boolean, name?: string): void {
    this.apiCallService
      .callNoError<UserResultJson[]>(h => this.projectApi.listProjectMembersUsingGETWithHttpInfo(this.projectId, h))
      .subscribe(users => {
        this.refresh = true;
        this.users = users;
        this.rows = users;
        this.loading = false;
        this.refresh = false;
      });
  }

  updateFilter(event: any) {
    let val = event.target.value.toLocaleLowerCase();
    this.temp = this.users;
    // filter our data
    let temp = this.temp.filter(e => e.name.toLocaleLowerCase().indexOf(val) >= 0 ||
      e.username.toLocaleLowerCase().indexOf(val) >= 0 ||
      e.mail.toLocaleLowerCase().indexOf(val) >= 0 ||
      e.role.toString().toLocaleLowerCase().indexOf(val) >= 0);

    // update the rows
    this.rows = temp;
  }


}
