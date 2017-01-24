import {AppComponent} from "./app.component";
import {routing} from "./app.routing";
import {NgModule} from "@angular/core";
import {LayoutModule} from "./layout/layout.module";
import {BrowserModule} from "@angular/platform-browser";
import {PagesModule} from "./pages/pages.module";
import {HttpModule} from "@angular/http";
@NgModule({
    imports: [BrowserModule, LayoutModule, routing, PagesModule, HttpModule],
    declarations: [AppComponent],
    bootstrap: [AppComponent]
})
export class AppModule {

}