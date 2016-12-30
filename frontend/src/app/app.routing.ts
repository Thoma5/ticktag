import {RouterModule, Routes} from '@angular/router';
import {WhoamiComponent} from './ui/whoami/whoami.component';
import {LoginComponent} from './ui/login/login.component';
import {HomeComponent} from './ui/home/home.component';
import {UsersComponent} from './ui/users/users.component';
import {ProjectsComponent} from './ui/projects/projects.component';
import {ProjectUsersComponent} from './ui/projects/user/project-users.component';
import {MemberAddComponent} from './ui/projects/user/add/member-add.component';
import {TicketOverviewComponent} from './ui/ticket-overview/ticket-overview.component';
import {TicketDetailComponent} from './ui/ticket-detail/ticket-detail.component';
import {BurnDownChartComponent} from './ui/burn-down-chart/burn-down-chart.component';

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'whoami', component: WhoamiComponent},
  {path: 'users', component: UsersComponent},
  {path: 'projects', component: ProjectsComponent},
  {path: 'project/:projectId/users', component: ProjectUsersComponent},
  {path: 'project/:projectId/users/add', component: MemberAddComponent},
  {path: 'project/:projectId/tickets', component: TicketOverviewComponent},
  {path: 'project/:projectId/ticket/:ticketNumber', component: TicketDetailComponent},
  {path: 'project/:projectId/burndown', component: BurnDownChartComponent},
];
export const routing = RouterModule.forRoot(routes);
