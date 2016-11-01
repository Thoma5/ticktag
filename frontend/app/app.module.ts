import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {AppComponent} from "./app.component";
import {AppRoutingModule} from "./app-routing.module";
import {HttpModule} from "@angular/http";

@NgModule({
    imports: [
        BrowserModule,
        AppRoutingModule,
    ],
    declarations: [
        AppComponent,
    ],
    bootstrap: [AppComponent],
})
export class AppModule {
}
