import {AppComponent} from './app.component';
import {NgModule} from '@angular/core';
import {LayoutModule} from './layout/layout.module';
import {PagesModule} from './pages/pages.module';
import {BrowserModule, HAMMER_GESTURE_CONFIG} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ProfileModule} from './profile/profile.module';
import {UserService} from './pages/profile/user.service';
import {PresentationService} from './profile/shared/presentation.service';
import {PartnerService} from './pages/partners/shared/partner.service';
import {HammerConfig} from './HammerConfig';
import {ParticipantService} from './admin/participants/participant.service';
import {RegistrationModule} from './pages/registration/registration.module';
import {ConfirmationService} from './shared/confirmation.service';
import {NgLoadingBarModule} from 'ng-loading-bar';
import {AgendaService} from './pages/agenda/shared/agenda.service';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {TokenInterceptor} from './security/token-interceptor.service';
import {BaseUrlInterceptor} from './shared/base-url-interceptor.service';
import {AppRoutingModule} from './app-routing.module';


@NgModule({
  imports: [LayoutModule, PagesModule, BrowserModule, BrowserAnimationsModule,
    ProfileModule, AppRoutingModule, RegistrationModule,
    NgLoadingBarModule.forRoot(), HttpClientModule
  ],
  providers: [UserService, PresentationService, PartnerService, ParticipantService, ConfirmationService, AgendaService,
    {
      provide: HAMMER_GESTURE_CONFIG,
      useClass: HammerConfig,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: BaseUrlInterceptor,
      multi: true,
    }],
  declarations: [AppComponent],
  bootstrap: [AppComponent]
})
export class AppModule {

}
