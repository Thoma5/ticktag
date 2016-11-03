import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {AppComponent} from "./app.component";
import {AppRoutingModule} from "./app-routing.module";
import {LoginComponent} from "./login/login.component";
import {FormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";
import {WhoamiComponent} from "./whoami/whoami.component";

@NgModule({
    imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule,
        HttpModule,
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
