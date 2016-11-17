import { NgModule, ApplicationRef } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpModule } from '@angular/http';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { routing } from './app.routing';

import { removeNgStyles, createNewHosts } from '@angularclass/hmr';
import {AuthService, ApiCallService} from './service';
import {LoginComponent} from './ui/login/login.component';
import {WhoamiComponent} from './ui/whoami/whoami.component';
import {HomeComponent} from './ui/home/home.component';
import {AuthApi} from './api/api/AuthApi';
import {Ng2Webstorage} from 'ng2-webstorage/dist/app';
import {UsersComponent} from './ui/users/users.component';
import {UserCreateComponent} from './ui/users/user-create.component';
import {UserApi} from './api/api/UserApi';
import {ProjectsComponent} from './ui/projects/projects.component';
import {ProjectCreateComponent} from './ui/projects/project-create.component';
import {ProjectApi} from './api/api/ProjectApi';
import {TicketDetailComponent} from './ui/ticket-detail/ticket-detail.component';
import {TicketSidebarComponent} from './ui/ticket-detail/ticket-sidebar.component';
import {TicketCoreComponent} from './ui/ticket-detail/ticket-core.component';
import { Ng2BootstrapModule } from 'ng2-bootstrap/ng2-bootstrap';
import { Angular2DataTableModule } from 'angular2-data-table';
import { MaterialModule } from '@angular/material';
import {AssignedUserComponent} from './ui/ticket-detail/assigned-user.component';
import { HumanizeDurationPipe } from './util/humanize-duration.pipe';
import { FormatMomentPipe } from './util/format-moment.pipe';


@NgModule({
  imports: [
    BrowserModule,
    HttpModule,
    FormsModule,
    Ng2Webstorage,
    routing,
    Angular2DataTableModule,
    Ng2BootstrapModule,
    MaterialModule.forRoot(),
  ],
  declarations: [
    AppComponent,
    HomeComponent,
    LoginComponent,
    WhoamiComponent,
    UsersComponent,
    UserCreateComponent,
    ProjectsComponent,
    ProjectCreateComponent,
    TicketDetailComponent,
    TicketSidebarComponent,
    TicketCoreComponent,
    AssignedUserComponent,

    HumanizeDurationPipe,
    FormatMomentPipe,
  ],
  providers: [
    AuthApi,
    UserApi,
    ProjectApi,
    ApiCallService,
    AuthService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor(public appRef: ApplicationRef) {}
  hmrOnInit(store: any) {
    console.log('HMR store', store);
  }
  hmrOnDestroy(store: any) {
    let cmpLocation = this.appRef.components.map(cmp => cmp.location.nativeElement);
    // recreate elements
    store.disposeOldHosts = createNewHosts(cmpLocation);
    // remove styles
    removeNgStyles();
  }
  hmrAfterDestroy(store: any) {
    // display new elements
    store.disposeOldHosts();
    delete store.disposeOldHosts;
  }
}
