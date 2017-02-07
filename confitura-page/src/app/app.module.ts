import {AppComponent} from "./app.component";
import {routing} from "./app.routing";
import {NgModule} from "@angular/core";
import {LayoutModule} from "./layout/layout.module";
import {BrowserModule} from "@angular/platform-browser";
import {PagesModule} from "./pages/pages.module";
import {HttpModule} from "@angular/http";
import {PersonModalComponent} from "./components/person-modal/person-modal.component";
@NgModule({
    imports: [BrowserModule, LayoutModule, routing, PagesModule, HttpModule],
    declarations: [AppComponent],
    bootstrap: [AppComponent]
})
export class AppModule {

}