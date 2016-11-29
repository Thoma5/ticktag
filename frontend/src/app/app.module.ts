import { NgModule, ApplicationRef } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpModule } from '@angular/http';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { routing } from './app.routing';

import { ModalModule } from 'angular2-modal';
import { BootstrapModalModule } from 'angular2-modal/plugins/bootstrap';

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
  EditTextviewStringComponent, EditTextviewStringEditComponent, EditTextviewStringReadComponent
} from './util/edit-textview/edit-textview-string.component';
import {
  EditTextviewPosNumberComponent, EditTextviewPosNumberEditComponent, EditTextviewPosNumberReadComponent
} from './util/edit-textview/edit-textview-posnumber.component';
import {
  EditTextviewDateTimeComponent, EditTextviewDateTimeEditComponent, EditTextviewDateTimeReadComponent
} from './util/edit-textview/edit-textview-datetime.component';
import {
  EditTextviewTimeComponent, EditTextviewTimeEditComponent, EditTextviewTimeReadComponent
} from './util/edit-textview/edit-textview-time.component';
import { OpenClosedButtonComponent } from './ui/ticket-detail/open-closed-button/open-closed-button.component';
import { TicketCommentsComponent } from './ui/ticket-detail/ticket-comments/ticket-comments.component';
import { TicketCommentComponent } from './ui/ticket-detail/ticket-comment/ticket-comment.component';
import { TicketCommentInputComponent } from './ui/ticket-detail/ticket-comment-input/ticket-comment-input.component';
import { TicketStorypointsComponent } from './ui/ticket-detail/ticket-storypoints/ticket-storypoints.component';
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
import { MarkdownTextviewReadComponent } from './util/markdown-textview/markdown-textview-read.component';
import { AutosizeTextareaDirective } from './util/autosize-textarea.directive';
import { FocusDirective } from './util/focus.directive';
import { EventFocusDirective } from './util/event-focus.directive';
import { TaginputComponent } from './util/taginput/taginput.component';
import { SelectAllDirective } from './util/select-all.directive';
import { EditButtonComponent } from './util/edit-button/edit-button.component';
import { LoadingComponent } from './util/loading/loading.component';
import { SpinnerComponent } from './util/spinner/spinner.component';
import { CommandTextviewComponent } from './ui/ticket-detail/command-textview/command-textview.component';
import { CommandDescriptionComponent } from './ui/ticket-detail/command-description/command-description.component';
import { CommandHelpComponent } from './ui/ticket-detail/command-help/command-help.component';

import {TicketEventsComponent} from './ui/ticket-detail/ticket-events/ticket-events.component';
import {TicketEventOldNewComponent} from './ui/ticket-detail/ticket-events/ticket-event-old-new/ticket-event-old-new.component';
import {TicketeventApi} from './api/api/TicketeventApi';
import {TicketEventComponent} from './ui/ticket-detail/ticket-events/ticket-event/ticker-event.component';
import {TicketEventUserComponent} from './ui/ticket-detail/ticket-events/ticket-event-user/ticket-event-user.component';
import {TicketEventParentChangedComponent}
  from './ui/ticket-detail/ticket-events/ticket-event-parent-ticket-changed/ticket-event-parent-ticket-changed.component';
import {TicketEventTagComponent} from './ui/ticket-detail/ticket-events/ticket-event-tag/ticket-event-tag.component';
import {TicketEventOldNewMarkupComponent} from "./ui/ticket-detail/ticket-events/ticket-event-old-new-markup/ticket-event-old-new-markup.component";


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
    ModalModule.forRoot(),
    BootstrapModalModule,
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
    EditTextviewStringComponent,
    EditTextviewStringEditComponent,
    EditTextviewStringReadComponent,
    EditTextviewPosNumberComponent,
    EditTextviewPosNumberEditComponent,
    EditTextviewPosNumberReadComponent,
    EditTextviewDateTimeComponent,
    EditTextviewDateTimeEditComponent,
    EditTextviewDateTimeReadComponent,
    EditTextviewTimeComponent,
    EditTextviewTimeEditComponent,
    EditTextviewTimeReadComponent,
    OpenClosedButtonComponent,
    TicketStorypointsComponent,
    AssignedUserComponent,
    SubticketsComponent,
    SubticketComponent,
    SubticketAddComponent,
    TaginputComponent,
    TicketCommentsComponent,
    TicketCommentComponent,
    TicketEventsComponent,
    TicketEventComponent,
    TicketEventOldNewComponent,
    TicketEventOldNewMarkupComponent,
    TicketEventUserComponent,
    TicketEventParentChangedComponent,
    TicketEventTagComponent,
    TicketCommentInputComponent,
    EditButtonComponent,
    CommandTextviewComponent,
    CommandDescriptionComponent,
    CommandHelpComponent,
    MarkdownTextviewReadComponent,

    HumanizeDurationPipe,
    FormatMomentPipe,
    MarkdownToHtmlPipe,
    AutosizeTextareaDirective,
    FocusDirective,
    EventFocusDirective,
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
    TicketeventApi,
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
