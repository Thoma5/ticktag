import { Component, OnInit, ViewChild, ViewContainerRef, OnDestroy, NgZone } from '@angular/core';
import { Location } from '@angular/common';
import { Title } from '@angular/platform-browser';
import '../style/app.scss';
import { AuthService, ApiCallService, User, ErrorHandler } from './service';
import { ProjectApi, ProjectResultJson, ProjectUserResultJson, PageProjectResultJson } from './api';
import { Router, ActivatedRoute, NavigationStart } from '@angular/router';
import { Overlay } from 'angular2-modal';
import { Modal } from 'angular2-modal/plugins/bootstrap';
import { Response } from '@angular/http';
import * as $ from 'jquery';
import * as _ from 'lodash';
import * as imm from 'immutable';
import { Observable } from 'rxjs';


@Component({
  selector: 'tt-app',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy, ErrorHandler {
  @ViewChild('start') sidebar: any;
  private showLoginButton: boolean;
  private user: User;
  private directTicketLinkEvent: (eventObject: JQueryEventObject) => any;
  private loadingProject: boolean = false;
  private _project: ProjectResultJson | null = null;
  private projectRole: ProjectUserResultJson.ProjectRoleEnum | null = null;
  private userProjects: imm.List<ProjectResultJson> | null = null;

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private modal: Modal,
    private overlay: Overlay,
    private vcRef: ViewContainerRef,
    private router: Router,
    private title: Title,
    private location: Location,
    private zone: NgZone,
    private apiCallService: ApiCallService,
    private projectApi: ProjectApi) {

    apiCallService.initErrorHandler(this);
    overlay.defaultViewContainer = vcRef;
  }

  set project(val: ProjectResultJson | null) {
    this._project = val;
    if (val != null) {
      this.title.setTitle(val.name + ' | TickTag');
    } else {
      this.title.setTitle('TickTag');
    }
  }

  get project(): ProjectResultJson | null {
    return this._project;
  }

  ngOnInit(): void {
    this.userObservable()
      .subscribe(user => this.user = user);

    this.userObservable()
      .flatMap(u => {
        if (u == null) {
          return Observable.of(null);
        } else {
          return this.loadUserProjects(u.id)
            .catch((err: any) => {
              console.log('Error loading user projects');
              console.dir(err);
              return Observable.of(null);
            });
        }
      })
      .subscribe(userProjects => {
        this.userProjects = userProjects;
      });

    this.router.events
      .filter(e => e instanceof NavigationStart)
      .map(e => e.url)
      .map(url => (url.indexOf('login') < 0) ? this.showLoginButton = true : this.showLoginButton = false )
      .distinctUntilChanged()
      .subscribe(result => { });

    this.router.events
      .filter(e => e instanceof NavigationStart)
      .map(e => e.url)
      .map(url => projectIdFromUrl(url))
      .distinctUntilChanged()
      .combineLatest(this.userObservable())
      .switchMap(projectIdAndUser => {
        let projectId = projectIdAndUser[0];
        let user = projectIdAndUser[1];
        this.loadingProject = true;
        if (user == null) {
          return Observable.of([null, null]);
        }
        if (projectId != null) {
          // TODO do we need better error handling here?
          return this.loadProjectInfo(projectId, user.id)
            .catch((err: any) => {
              console.log('Error loading project');
              console.dir(err);
              return Observable.empty<[ProjectResultJson, ProjectUserResultJson.ProjectRoleEnum]>();
            });
        } else {
          return Observable.of([null, null]);
        }
      })
      .subscribe(projectAndMembership => {
        this.project = projectAndMembership[0];
        this.projectRole = projectAndMembership[1] != null ? projectAndMembership[1].projectRole : null;
        this.loadingProject = false;
      });

    $(document).on('click', 'a.grammar-htmlifyCommands', this.directTicketLinkEvent = (e) => {
      e.preventDefault();
      this.zone.run(() => {
        this.router.navigate([
          '/project',
          e.currentTarget.getAttribute('data-projectId'),
          'ticket',
          e.currentTarget.getAttribute('data-ticketNumber'),
        ]);
      });
    });
  }

  private userObservable(): Observable<User> {
    return Observable.concat(Observable.of(this.authService.user), this.authService.observeUser());
  }

  ngOnDestroy() {
    if (this.directTicketLinkEvent) {
      $(document).off('click', 'a.grammar-htmlifyCommands', this.directTicketLinkEvent);
    }
  }

  loadProjectInfo(id: string, userId: string): Observable<[ProjectResultJson, ProjectUserResultJson]> {
    return Observable.zip(this.apiCallService.callNoError(p => this.projectApi.getProjectUsingGETWithHttpInfo(id, p)),
                          this.apiCallService.callNoError(p => this.projectApi.listProjectMembersUsingGETWithHttpInfo(id, false, p))
                            .map((members: ProjectUserResultJson[]) => _.filter(members, member => member.id === userId)[0])
                          );
  }

  loadUserProjects(userId: string): Observable<imm.List<ProjectResultJson>> {
    return this.apiCallService
      .callNoError(p => this.projectApi.listProjectsUsingGETWithHttpInfo(0, 10, 'NAME', true, null, false, false, p))
      .map((p: PageProjectResultJson) => imm.List(p.content));
  }

  logout(): void {
    this.clearUser();
    this.gotoHome();
  }

  onError(resp: any): void {
    console.dir(resp);
    if (resp instanceof Response) {
      if (statusGroup(resp.status) === 500) {
        console.log('Error: server');
        this.serverError(resp);
      } else if (statusGroup(resp.status) === 400) {
        console.log('Error: client');
        this.clientError(resp);
      } else {
        console.log('Error: other');
        this.otherError(resp);
      }
    } else {
      console.log('Error: unknown');
      this.unknownError(resp);
    }
  }

  private clientError(resp: Response): void {
    if (resp.status === 401) {
      console.log('Client error: unauthenticated');
      this.unauthenticatedError(resp);
    } else if (resp.status === 403) {
      console.log('Client error: unauthorized');
      this.unauthorizedError(resp);
    } else if (resp.status === 404) {
      console.log('Client error: not found');
      this.notFoundError(resp);
    } else {
      console.log('Client error: other');
      this.otherError(resp);
    }
  }

  private unauthenticatedError(resp: Response): void {
    this.authService.user = null;
    this.gotoLogin();
  }

  private unauthorizedError(resp: Response): void {
    // TODO the backend should respond with unauthenticated, but this is an acceptable workaround
    if (this.user == null) {
      console.log('User is not logged in, switching to unauthenticated');
      this.unauthenticatedError(resp);
      return;
    }

    this.gotoHome();
  }

  private notFoundError(resp: Response): void {
    this.modal.alert()
      .size('sm')
      .title('Not found')
      .body('Sorry. Whatever you are looking for...it\'s not here')
      .okBtn('Take me back')
      .open()
      .then(promise => {
        promise.result.then(result => this.goBack());
      });
  }

  private serverError(resp: Response): void {
    let bodyHtml = '<p>';
    bodyHtml += 'Sorry, something went terribly wrong. Please contact us so we can fix it';
    bodyHtml += '</p>';
    bodyHtml += '<code>';
    bodyHtml += resp;
    bodyHtml += '</code>';

    this.modal.alert()
      .size('sm')
      .title('Server error')
      .body(bodyHtml)
      .open()
      .then(promise => {
        promise.result.then(result => this.gotoHome());
      });
  }

  private otherError(resp: Response): void {
    this.unknownError(resp);
  }

  private unknownError(resp: any): void {
    let bodyHtml = '<p>';
    bodyHtml += 'We don\'t know what just happened but it\'s not good. Please contact us so we can fix it';
    bodyHtml += '</p>';
    bodyHtml += '<hr />';
    bodyHtml += '<code>';
    bodyHtml += resp;
    bodyHtml += '</code>';

    this.modal.alert()
      .size('lg')
      .title('Unknown error')
      .body(bodyHtml)
      .open()
      .then(promise => {
        promise.result.then(result => this.gotoHome());
      });
  }

  private gotoLogin() {
    this.router.navigate(['/login']);
  }

  private gotoHome() {
    this.router.navigate(['/']);
  }

  private goBack() {
    this.location.back();
  }

  private clearUser() {
    this.authService.user = null;
  }
}

function projectIdFromUrl(url: string): string | null {
  // Better solutions are very welcome
  let re = /^.*\/project\/(.*?)\/.*$/g;
  let matches = re.exec(url);
  if (matches != null && matches.length > 1) {
    return matches[1];
  }

  return null;
}

function statusGroup(statusCode: number) {
  return Math.floor(statusCode / 100) * 100;
}
