import { NgModule, ApplicationRef } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpModule } from '@angular/http';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { routing } from './app.routing';

import { ModalModule } from 'angular2-modal';
import { BootstrapModalModule } from 'angular2-modal/plugins/bootstrap';
import { DragulaModule } from 'ng2-dragula';

import { removeNgStyles, createNewHosts } from '@angularclass/hmr';
import { AuthService, ApiCallService, MarkdownService, ImagesService } from './service';
import { LoginComponent } from './ui/login/login.component';
import { WhoamiComponent } from './ui/whoami/whoami.component';
import { HomeComponent } from './ui/home/home.component';
import { Ng2Webstorage } from 'ng2-webstorage/dist/app';
import { UsersComponent } from './ui/users/users.component';
import { UserCreateComponent } from './ui/users/create/user-create.component';
import { UserUpdateComponent } from './ui/users/update/user-update.component';
import { ProjectsComponent } from './ui/projects/projects.component';
import { ProjectCreateComponent } from './ui/projects/create/project-create.component';
import { ProjectUpdateComponent } from './ui/projects/update/project-update.component';
import { ProjectUsersComponent } from './ui/projects/user/project-users.component';
import { MemberAddComponent } from './ui/projects/user/add/member-add.component';
import { MemberUpdateComponent } from './ui/projects/user/update/member-update.component';
import {
  ProjectApi, AuthApi, UserApi, AssignmenttagApi, CommentsApi, MemberApi,
  TimecategoryApi, TicketApi, TickettagApi, TickettaggroupApi, GetApi,
  TicketuserrelationApi, TickettagrelationApi, LoggedtimeApi
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
import { TicketFilterComponent } from './ui/ticket-overview/ticket-filter/ticket-filter.component';
import { AssignedUserComponent } from './ui/ticket-detail/assigned-user/assigned-user.component';
import { SubticketsComponent } from './ui/ticket-detail/subtickets/subtickets.component';
import { SubticketComponent } from './ui/ticket-detail/subticket/subticket.component';
import { SubticketAddComponent } from './ui/ticket-detail/subticket-add/subticket-add.component';
import { Ng2BootstrapModule } from 'ng2-bootstrap/ng2-bootstrap';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { MaterialModule } from '@angular/material';
import { HumanizeDurationPipe } from './util/humanize-duration.pipe';
import { FormatMomentPipe } from './util/format-moment.pipe';
import { MarkdownToHtmlPipe } from './util/markdown-to-html.pipe';
import { PercentPipe } from './util/percent.pipe';
import { MarkdownTextviewReadComponent } from './util/markdown-textview/markdown-textview-read.component';
import { AutosizeTextareaDirective } from './util/autosize-textarea.directive';
import { FocusDirective } from './util/focus.directive';
import { EventFocusDirective } from './util/event-focus.directive';
import { TaginputComponent } from './util/taginput/taginput.component';
import { TagViewComponent } from './ui/ticket-overview/tagview/tagview.component';
import { SelectAllDirective } from './util/select-all.directive';
import { EditButtonComponent } from './util/edit-button/edit-button.component';
import { LoadingComponent } from './util/loading/loading.component';
import { TicketOverviewComponent } from './ui/ticket-overview/ticket-overview.component';
import { AssignedUserOverviewComponent } from './ui/ticket-overview/assigned-user/assigned-user-overview.component';
import { SpinnerComponent } from './util/spinner/spinner.component';
import { CommandTextviewComponent } from './util/command-textview/command-textview.component';
import { CommandDescriptionComponent } from './util/command-description/command-description.component';
import { CommandHelpComponent } from './util/command-help/command-help.component';
import { ProgressBarComponent } from './util/progressbar/progressbar.component';
import { UserImageComponent } from './util/user-image/user-image.component';

import { TicketEventsComponent } from './ui/ticket-detail/ticket-events/ticket-events.component';
import { TicketEventOldNewComponent } from './ui/ticket-detail/ticket-events/ticket-event-old-new/ticket-event-old-new.component';
import { TicketeventApi } from './api/api/TicketeventApi';
import { GroupedTicketEventComponent } from './ui/ticket-detail/ticket-events/grouped-ticket-event/grouped-ticket-event.component';
import { TicketEventUserComponent } from './ui/ticket-detail/ticket-events/ticket-event-user/ticket-event-user.component';
import { TicketEventParentChangedComponent }
  from './ui/ticket-detail/ticket-events/ticket-event-parent-ticket-changed/ticket-event-parent-ticket-changed.component';
import { TicketEventTagComponent } from './ui/ticket-detail/ticket-events/ticket-event-tag/ticket-event-tag.component';
import { TicketEventOldNewMarkupComponent }
  from './ui/ticket-detail/ticket-events/ticket-event-old-new-markup/ticket-event-old-new-markup.component';
import { KanbanBoardsComponent } from './ui/kanban-boards/kanban-boards.component';
import { BoardApi } from './api/api/BoardApi';
import { KanbanBoardDetailComponent } from './ui/kanban-board-detail/kanban-board-detail.component';
import { KanbanCellComponent } from './ui/kanban-board-detail/kanban-cell/kanban-cell.component';
import { BurnDownChartComponent } from './ui/burn-down-chart/burn-down-chart.component';
import { NKDatetimeModule } from 'ng2-datetime/ng2-datetime';
import { ChartsModule } from 'ng2-charts/ng2-charts';

import { TicketCreateComponent } from './ui/ticket-detail/ticket-create/ticket-create.component';
import { TicketEventMentionComponent } from './ui/ticket-detail/ticket-events/ticket-event-mention/ticket-event-mention.component';
import { ImagePickerComponent } from './util/image-picker/image-picker.component';
import { SidebarComponent } from './ui/sidebar/sidebar.component';
import { AssignmentTagsComponent } from './ui/assignment-tags/assignment-tags.component';
import { AssignmentTagCreateComponent } from './ui/assignment-tags/create/assignment-tag-create.component';
import { AssignmentTagUpdateComponent } from './ui/assignment-tags/update/assignment-tag-update.component';
import { TicketTagsComponent } from './ui/ticket-tags/ticket-tags.component';
import { TicketTagCreateComponent } from './ui/ticket-tags/create/ticket-tag-create.component';
import { TicketTagUpdateComponent } from './ui/ticket-tags/update/ticket-tag-update.component';
import { UserProfileComponent } from './ui/user-profile/user-profile.component';

@NgModule({
  imports: [
    BrowserModule,
    HttpModule,
    FormsModule,
    Ng2Webstorage,
    routing,
    NgxDatatableModule,
    Ng2BootstrapModule,
    MaterialModule.forRoot(),
    ModalModule.forRoot(),
    BootstrapModalModule,
    DragulaModule,
    NKDatetimeModule,
    ChartsModule
  ],
  declarations: [
    AppComponent,
    HomeComponent,
    LoginComponent,
    WhoamiComponent,
    UsersComponent,
    UserCreateComponent,
    UserUpdateComponent,
    ProjectsComponent,
    ProjectCreateComponent,
    ProjectUpdateComponent,
    ProjectUsersComponent,
    MemberAddComponent,
    MemberUpdateComponent,
    TicketOverviewComponent,
    TicketDetailComponent,
    TicketSidebarComponent,
    TicketCoreComponent,
    TicketFilterComponent,
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
    AssignedUserComponent,
    AssignedUserOverviewComponent,
    SubticketsComponent,
    SubticketComponent,
    SubticketAddComponent,
    TaginputComponent,
    TagViewComponent,
    TicketCommentsComponent,
    TicketCommentComponent,
    TicketEventsComponent,
    GroupedTicketEventComponent,
    TicketEventOldNewComponent,
    TicketEventOldNewMarkupComponent,
    TicketEventUserComponent,
    TicketEventParentChangedComponent,
    TicketEventTagComponent,
    TicketEventMentionComponent,
    TicketCommentInputComponent,
    EditButtonComponent,
    CommandTextviewComponent,
    CommandDescriptionComponent,
    CommandHelpComponent,
    MarkdownTextviewReadComponent,
    ProgressBarComponent,
    BurnDownChartComponent,
    TicketCreateComponent,
    SidebarComponent,
    AssignmentTagsComponent,
    AssignmentTagCreateComponent,
    AssignmentTagUpdateComponent,
    TicketTagsComponent,
    TicketTagUpdateComponent,
    TicketTagCreateComponent,
    UserProfileComponent,

    HumanizeDurationPipe,
    FormatMomentPipe,
    MarkdownToHtmlPipe,
    PercentPipe,
    AutosizeTextareaDirective,
    FocusDirective,
    EventFocusDirective,
    SelectAllDirective,
    LoadingComponent,
    SpinnerComponent,

    KanbanBoardsComponent,
    KanbanBoardDetailComponent,
    KanbanCellComponent,
    UserImageComponent,
    ImagePickerComponent,

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
    BoardApi,
    LoggedtimeApi,
    ApiCallService,
    AuthService,
    MarkdownService,
    ImagesService,
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
