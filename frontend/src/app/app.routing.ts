import {RouterModule, Routes} from '@angular/router';
import {WhoamiComponent} from './ui/whoami/whoami.component';
import {LoginComponent} from './ui/login/login.component';
import {HomeComponent} from './ui/home/home.component';
import {UsersComponent} from './ui/users/users.component';
import {ProjectsComponent} from './ui/projects/projects.component';
import {TicketOverviewComponent} from './ui/ticket-overview/ticket-overview.component';
import {TicketDetailComponent} from './ui/ticket-detail/ticket-detail.component';

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'whoami', component: WhoamiComponent},
  {path: 'users', component: UsersComponent},
  {path: 'projects', component: ProjectsComponent},
  {path: 'project/:projectId/tickets', component: TicketOverviewComponent},
  {path: 'project/:projectId/ticket/:ticketNumber', component: TicketDetailComponent},
];
export const routing = RouterModule.forRoot(routes);
