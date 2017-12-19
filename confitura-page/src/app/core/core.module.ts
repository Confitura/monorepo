import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {LoginService} from './security/login.service';
import {CurrentUser} from './security/current-user.service';
import {RouterModule} from '@angular/router';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {ConfirmationService} from './confirmation.service';
import {BaseUrlInterceptor} from '../shared/base-url-interceptor.service';
import {AgendaService} from '../pages/agenda/shared/agenda.service';
import {PresentationService} from '../profile/shared/presentation.service';
import {ParticipantService} from '../admin/participants/participant.service';
import {TokenInterceptor} from './security/token-interceptor.service';
import {HammerConfig} from '../HammerConfig';
import {PartnerService} from '../pages/partners/shared/partner.service';
import {HAMMER_GESTURE_CONFIG} from '@angular/platform-browser';
import {NavigationComponent} from './navigation/navigation.component';
import {FooterComponent} from './footer/footer.component';
import {CookiesComponent} from './cookies/cookies.component';
import {MenuItemComponent} from './navigation/menu-item.component';
import {UserService} from './user/user.service';

@NgModule({
  imports: [CommonModule, HttpClientModule, RouterModule],
  declarations: [
    NavigationComponent,
    FooterComponent,
    CookiesComponent,
    MenuItemComponent,
  ],
  providers: [LoginService, CurrentUser, ConfirmationService, UserService, PresentationService,
    PartnerService, ParticipantService, AgendaService,
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
  exports: [CommonModule, RouterModule, NavigationComponent, FooterComponent, CookiesComponent]
})
export class CoreModule {

}
