import {AppComponent} from "./app.component";
import {routing} from "./app.routing";
import {NgModule} from "@angular/core";
import {LayoutModule} from "./layout/layout.module";
import {PagesModule} from "./pages/pages.module";
import {SharedModule} from "./shared/shared.module";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
@NgModule({
    imports: [LayoutModule, routing, PagesModule, SharedModule, BrowserModule, BrowserAnimationsModule],
    declarations: [AppComponent],
    bootstrap: [AppComponent]
})
export class AppModule {

}