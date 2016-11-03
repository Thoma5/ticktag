import {Component} from "@angular/core";
import {AuthApi} from "./api/api/AuthApi";

@Component({
    providers: [AuthApi],
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
