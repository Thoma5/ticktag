import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {AppComponent} from "./app.component";
import {AppRoutingModule} from "./app-routing.module";
import {LoginComponent} from "./login/login.component";
import {FormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";
import {WhoamiComponent} from "./whoami/whoami.component";
import {Ng2Webstorage} from "ng2-webstorage";

@NgModule({
    imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule,
        HttpModule,
        Ng2Webstorage,
    ],
    declarations: [
        AppComponent,
        LoginComponent,
        WhoamiComponent,
    ],
    bootstrap: [AppComponent],
})
export class AppModule {
}
