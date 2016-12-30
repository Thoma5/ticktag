import { Component, OnInit, OnChanges, Input, Output, EventEmitter } from '@angular/core';
import { ApiCallService, AuthService, User } from '../../service';
import { AuthApi, UserApi, PageUserResultJson, UserResultJson } from '../../api';
import { RoleResultJson } from '../../api/model/RoleResultJson';
import { WhoamiResultJson } from '../../api/model/WhoamiResultJson';
import { Subject } from 'rxjs/Subject';

@Component({
  selector: 'tt-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss'],
})
export class UsersComponent implements OnInit, OnChanges {
  iconsCss = {
    sortAscending: 'glyphicon glyphicon-chevron-down',
    sortDescending: 'glyphicon glyphicon-chevron-up',
    pagerLeftArrow: 'glyphicon glyphicon-chevron-left',
    pagerRightArrow: 'glyphicon glyphicon-chevron-right',
    pagerPrevious: 'glyphicon glyphicon-backward',
    pagerNext: 'glyphicon glyphicon-forward'
  };
  users: PageUserResultJson;
  loading = true;
  refresh = true;
  cu = false;
  disabled = false;
  mode = '';
  toUpdateUser: UserResultJson = undefined;
  sortprop = ['NAME_ASC'];
  offset = 0;
  limit = 30;
  rows: UserResultJson[] = [];
  totalElements = 0;
  filter= new Subject<string>();
  roles: RoleResultJson[];
  filterRole: string = '';
  private userFilter: string;
  private user: User;
  private me: WhoamiResultJson;
  @Input() assignMode: false;
  @Input() assignedUsers: UserResultJson[]= [];
  @Output() assignUser= new EventEmitter<UserResultJson>();

  constructor(
    private userApi: UserApi,
    private apiCallService: ApiCallService,
    private authApi: AuthApi,
    private authService: AuthService) {
  }
  ngOnChanges(changes: any): void {
    this.assignedUsers = changes.assignedUsers;
  }
  ngOnInit(): void {
    this.users = [];
    this.getRoles();
    this.getUsers();
    this.getWhoami();
    this.user = this.authService.user;
    this.authService.observeUser()
      .subscribe(user => {
        this.user = user;
      });
    this.filter.debounceTime(300).do(term  => this.getUsers(term)).subscribe(result => {}, error => {});
  }

  onPage(event: any) {
    this.limit = event.limit;
    this.offset = event.offset;
    this.getUsers();
  }

  onSort(event: any) {
    if (event.sorts[0].prop === 'name') {
      this.sortprop = ['NAME_' + event.sorts[0].dir.toUpperCase()];
    } else if (event.sorts[0].prop === 'username') {
      this.sortprop = ['USERNAME_' + event.sorts[0].dir.toUpperCase()];
    } else if (event.sorts[0].prop === 'mail') {
      this.sortprop = ['MAIL_' + event.sorts[0].dir.toUpperCase()];
    } else if (event.sorts[0].prop === 'role') {
      this.sortprop = ['ROLE_' + event.sorts[0].dir.toUpperCase()];
    }
    this.getUsers();
  }

  updateFilter(filter: any) {
    this.offset = 0;
    this.filter.next(filter.target.value);
  }

  getUsers(filter?: string): void {
    if (filter === undefined) {
      filter = this.userFilter;
    } else {
      this.userFilter = filter;
    }
    let filterRole = this.filterRole === '' ? undefined : this.filterRole;
    this.apiCallService
      .callNoError<PageUserResultJson>(h => this.userApi
        .listUsersUsingGETWithHttpInfo(this.offset, this.limit, this.sortprop, filter ? filter : '', filterRole, this.disabled, h))
      .subscribe(users => {
        this.refresh = true;
        this.users = users;
        this.totalElements = users.totalElements;
        const start = this.offset * this.limit;
        const end = start + this.limit;
        let rows = [...this.rows];
        for (let i = start; i < end; i++) {
          rows[i] = users.content[i - this.offset * this.limit];
        }
        this.rows = rows;
        this.loading = false;
        this.refresh = false;
      });
  }

  getRoles(): void {
    this.apiCallService
      .callNoError<RoleResultJson[]>(h => this.userApi.listRolesUsingGETWithHttpInfo(h))
      .subscribe(roles => {
        this.roles = roles;
      });
  }

  getWhoami(): void {
    this.apiCallService
      .callNoError<WhoamiResultJson>((h) => this.authApi.whoamiUsingGETWithHttpInfo(h))
      .subscribe(user => { this.me = user; });
  }

  onDeleteClicked(id: string) {
    this.apiCallService
      .call<any>(h => this.userApi.deleteUserUsingDELETEWithHttpInfo(id, h))
      .subscribe(param => {
        this.refresh = true;
        this.getUsers();
      }
      );
  }

  onStartCreate() {
    this.mode = 'Create';
    this.cu = true;
  }
  onStartUpdate(user: UserResultJson) {
    this.toUpdateUser = user;
    this.cu = true;
    this.mode = 'Update';
  }
  onStopCreate() {
    this.cu = false;
  }
  cuFinished() {
    this.cu = false;
    this.getUsers();
  }
  onAssignUser(user: UserResultJson) {
    this.assignUser.emit(user);

  }
}
