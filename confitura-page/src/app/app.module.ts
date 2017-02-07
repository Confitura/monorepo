import {AppComponent} from "./app.component";
import {routing} from "./app.routing";
import {NgModule} from "@angular/core";
import {LayoutModule} from "./layout/layout.module";
import {PagesModule} from "./pages/pages.module";
import {SharedModule} from "./shared/shared.module";
@NgModule({
    imports: [LayoutModule, routing, PagesModule, SharedModule],
    declarations: [AppComponent],
    bootstrap: [AppComponent]
})
export class AppModule {

}