import {AppComponent} from "./app.component";
import {routing} from "./app.routing";
import {NgModule} from "@angular/core";
import {LayoutModule} from "./layout/layout.module";
import {PagesModule} from "./pages/pages.module";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {ProfileModule} from "./profile/profile.module";
import {UserService} from "./pages/profile/user.service";
import {PresentationService} from "./profile/shared/presentation.service";
import {PartnerService} from "./pages/partners/shared/partner.service";
import {AdminModule} from "./admin/admin.module";
@NgModule({
    imports: [LayoutModule, PagesModule, BrowserModule, BrowserAnimationsModule, ProfileModule, AdminModule, routing],
    providers: [UserService, PresentationService, PartnerService],
    declarations: [AppComponent],
    bootstrap: [AppComponent]
})
export class AppModule {

}