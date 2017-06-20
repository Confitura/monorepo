import {AppComponent} from "./app.component";
import {routing} from "./app.routing";
import {NgModule} from "@angular/core";
import {LayoutModule} from "./layout/layout.module";
import {PagesModule} from "./pages/pages.module";
import {BrowserModule, HAMMER_GESTURE_CONFIG} from "@angular/platform-browser";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {ProfileModule} from "./profile/profile.module";
import {UserService} from "./pages/profile/user.service";
import {PresentationService} from "./profile/shared/presentation.service";
import {PartnerService} from "./pages/partners/shared/partner.service";
import {AdminModule} from "./admin/admin.module";
import {V4pModule} from "./pages/v4p/v4p.module";
import {HotkeyModule} from "angular2-hotkeys";
import {HammerConfig} from "./HammerConfig";
import {ParticipantService} from "./admin/participants/participant.service";
import {RegistrationModule} from "./pages/registration/registration.module";
import {ConfirmationService} from "./shared/confirmation.service";
import {NgLoadingBarModule} from "ng-loading-bar";


@NgModule({
    imports: [LayoutModule, PagesModule, BrowserModule, BrowserAnimationsModule,
        ProfileModule, AdminModule, V4pModule, routing, HotkeyModule.forRoot(), RegistrationModule,
        NgLoadingBarModule.forRoot()
    ],
    providers: [UserService, PresentationService, PartnerService, ParticipantService, ConfirmationService,
        {
            provide: HAMMER_GESTURE_CONFIG,
            useClass: HammerConfig,
        }],
    declarations: [AppComponent],
    bootstrap: [AppComponent]
})
export class AppModule {

}