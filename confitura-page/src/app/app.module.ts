import {AppComponent} from "./app.component";
import {routing} from "./app.routing";
import {NgModule} from "@angular/core";
import {LayoutModule} from "./layout/layout.module";
import {BrowserModule} from "@angular/platform-browser";
@NgModule({
    imports: [BrowserModule, LayoutModule],
    declarations: [AppComponent],
    bootstrap: [AppComponent]
})
export class AppModule {

}