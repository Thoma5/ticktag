import { NgModule, ApplicationRef } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpModule } from '@angular/http';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { routing } from './app.routing';

import { removeNgStyles, createNewHosts } from '@angularclass/hmr';
import { AuthService, ApiCallService, MarkdownService } from './service';
import { LoginComponent } from './ui/login/login.component';
import { WhoamiComponent } from './ui/whoami/whoami.component';
import { HomeComponent } from './ui/home/home.component';
import { Ng2Webstorage } from 'ng2-webstorage/dist/app';
import { UsersComponent } from './ui/users/users.component';
import { UserCreateComponent } from './ui/users/user-create.component';
import { ProjectsComponent } from './ui/projects/projects.component';
import { ProjectCreateComponent } from './ui/projects/project-create.component';
import {
  ProjectApi, AuthApi, UserApi, AssignmenttagApi, CommentsApi, MemberApi,
  TimecategoryApi, TicketApi, TickettagApi, TickettaggroupApi, GetApi,
  TicketuserrelationApi, TickettagrelationApi
} from './api';
import { TicketDetailComponent } from './ui/ticket-detail/ticket-detail.component';
import { TicketSidebarComponent } from './ui/ticket-detail/ticket-sidebar/ticket-sidebar.component';
import { TicketCoreComponent } from './ui/ticket-detail/ticket-core/ticket-core.component';
import { EditableTextviewComponent } from './util/edit-textview/edit-textview.component';
import {
  TitleTextviewReadComponent,
  TitleTextviewEditComponent
} from './ui/ticket-detail/ticket-title-textview/ticket-title-textview.component';
import { MarkdownTextviewEditComponent } from './util/markdown-textview/markdown-textview-edit.component';
import { MarkdownTextviewReadComponent } from './util/markdown-textview/markdown-textview-read.component';
import { OpenClosedButtonComponent } from './ui/ticket-detail/open-closed-button/open-closed-button.component';
import { TicketCommentsComponent } from './ui/ticket-detail/ticket-comments/ticket-comments.component';
import { TicketCommentComponent } from './ui/ticket-detail/ticket-comment/ticket-comment.component';
import { TicketCommentInputComponent } from './ui/ticket-detail/ticket-comment-input/ticket-comment-input.component';
import {
  TicketStorypointsComponent,
  StorypointsTextviewEditComponent,
  StorypointsTextviewReadComponent
} from './ui/ticket-detail/ticket-storypoints/ticket-storypoints.component';
import { AssignedUserComponent } from './ui/ticket-detail/assigned-user/assigned-user.component';
import { SubticketsComponent } from './ui/ticket-detail/subtickets/subtickets.component';
import { SubticketComponent } from './ui/ticket-detail/subticket/subticket.component';
import { SubticketAddComponent } from './ui/ticket-detail/subticket-add/subticket-add.component';
import { Ng2BootstrapModule } from 'ng2-bootstrap/ng2-bootstrap';
import { Angular2DataTableModule } from 'angular2-data-table';
import { MaterialModule } from '@angular/material';
import { HumanizeDurationPipe } from './util/humanize-duration.pipe';
import { FormatMomentPipe } from './util/format-moment.pipe';
import { MarkdownToHtmlPipe } from './util/markdown-to-html.pipe';
import { JsonPipe } from './util/json.pipe';
import { AutosizeTextareaDirective } from './util/autosize-textarea.directive';
import { FocusDirective } from './util/focus.directive';
import { TaginputComponent } from './util/taginput/taginput.component';
import { SelectAllDirective } from './util/select-all.directive';
import { EditButtonComponent } from './util/edit-button/edit-button.component';
import { LoadingComponent } from './util/loading/loading.component';
import { SpinnerComponent } from './util/spinner/spinner.component';
import { CommandTextviewComponent } from './ui/ticket-detail/command-textview/command-textview.component';


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
    EditableTextviewComponent,
    TitleTextviewReadComponent,
    TitleTextviewEditComponent,
    MarkdownTextviewReadComponent,
    MarkdownTextviewEditComponent,
    OpenClosedButtonComponent,
    TicketStorypointsComponent,
    StorypointsTextviewEditComponent,
    StorypointsTextviewReadComponent,
    AssignedUserComponent,
    SubticketsComponent,
    SubticketComponent,
    SubticketAddComponent,
    TaginputComponent,
    TicketCommentsComponent,
    TicketCommentComponent,
    TicketCommentInputComponent,
    EditButtonComponent,
    CommandTextviewComponent,

    HumanizeDurationPipe,
    FormatMomentPipe,
    MarkdownToHtmlPipe,
    JsonPipe,
    AutosizeTextareaDirective,
    FocusDirective,
    SelectAllDirective,
    LoadingComponent,
    SpinnerComponent,
  ],
  providers: [
    ProjectApi,
    AuthApi,
    UserApi,
    AssignmenttagApi,
    CommentsApi,
    MemberApi,
    TimecategoryApi,
    TicketApi,
    TickettagApi,
    TickettaggroupApi,
    GetApi,
    TicketuserrelationApi,
    TickettagrelationApi,

    ApiCallService,
    AuthService,
    MarkdownService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor(public appRef: ApplicationRef) { }
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
