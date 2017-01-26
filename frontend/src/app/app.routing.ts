import { RouterModule, Routes } from '@angular/router';
import { WhoamiComponent } from './ui/whoami/whoami.component';
import { LoginComponent } from './ui/login/login.component';
import { HomeComponent } from './ui/home/home.component';
import { UsersComponent } from './ui/users/users.component';
import { ProjectsComponent } from './ui/projects/projects.component';
import { ProjectUsersComponent } from './ui/projects/user/project-users.component';
import { MemberAddComponent } from './ui/projects/user/add/member-add.component';
import { TicketOverviewComponent } from './ui/ticket-overview/ticket-overview.component';
import { TicketDetailComponent } from './ui/ticket-detail/ticket-detail.component';
import { KanbanBoardsComponent } from './ui/kanban-boards/kanban-boards.component';
import { KanbanBoardDetailComponent } from './ui/kanban-board-detail/kanban-board-detail.component';
import { BurnDownChartComponent } from './ui/burn-down-chart/burn-down-chart.component';
import { AssignmentTagsComponent } from './ui/assignment-tags/assignment-tags.component';
import { TicketTagsComponent } from './ui/ticket-tags/ticket-tags.component';
import { UserProfileComponent } from './ui/user-profile/user-profile.component';
import { TimeCategoriesComponent } from './ui/time-categories/time-categories.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'whoami', component: WhoamiComponent },
  { path: 'users', component: UsersComponent },
  { path: 'profile', component: UserProfileComponent },
  { path: 'projects', component: ProjectsComponent },
  { path: 'project/:projectId/users', component: ProjectUsersComponent },
  { path: 'project/:projectId/users/add', component: MemberAddComponent },
  { path: 'project/:projectId/tickets', component: TicketOverviewComponent },
  { path: 'project/:projectId/ticket/:ticketNumber', component: TicketDetailComponent },
  { path: 'project/:projectId/burndown', component: BurnDownChartComponent },
  { path: 'project/:projectId/assignmenttags', component: AssignmentTagsComponent },
  { path: 'project/:projectId/tickettags', component: TicketTagsComponent },
  { path: 'project/:projectId/timecategories', component: TimeCategoriesComponent },
  { path: 'project/:projectId/boards', component: KanbanBoardsComponent },
  { path: 'project/:projectId/board/:boardId', component: KanbanBoardDetailComponent }
];
export const routing = RouterModule.forRoot(routes);
