import {RouterModule, Routes} from '@angular/router';
import {WhoamiComponent} from './whoami/whoami.component';
import {LoginComponent} from './login/login.component';
import {HomeComponent} from './home/home.component';
import {UsersComponent} from './users/users.component';

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'whoami', component: WhoamiComponent},
  {path: 'users', component: UsersComponent},
];
export const routing = RouterModule.forRoot(routes);
