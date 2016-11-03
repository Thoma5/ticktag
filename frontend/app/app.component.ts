import {Component} from "@angular/core";
import {AuthApi} from "./api/api/AuthApi";
import {AuthService} from "./service/auth-service.service";

@Component({
    providers: [AuthApi, AuthService],
    moduleId: module.id,
    selector: 'ticktag',
    template: `
    <h1>{{title}}</h1>
    <router-outlet></router-outlet>
`,
    styleUrls: ['app.component.css'],
})
export class AppComponent {
    title = 'TickTag';
}
