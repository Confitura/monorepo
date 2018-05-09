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
import {MatButtonModule, MatIconModule, MatProgressBarModule} from '@angular/material';
import {ProgressBarComponent} from './progress-bar/progress-bar.component';
import {ProgressBarService} from './progress-bar/progress-bar.service';
import {ProgressBarInterceptor} from './progress-bar/progress-bar.interceptor';
import {VoteStatsServiceService} from '../admin/votes/vote-list/vote-stats.service';
import {IsActiveGuard} from '../v4p/IsActiveGuard';

@NgModule({
  imports: [
    CommonModule,
    HttpClientModule,
    RouterModule,
    MatProgressBarModule,
    MatButtonModule,
    MatIconModule
  ],
  declarations: [
    NavigationComponent,
    FooterComponent,
    CookiesComponent,
    MenuItemComponent,
    ProgressBarComponent,
  ],
  providers: [
    LoginService,
    CurrentUser,
    ConfirmationService,
    UserService,
    PresentationService,
    PartnerService,
    ParticipantService,
    VoteStatsServiceService,
    AgendaService,
    ProgressBarService,
    IsActiveGuard,
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
    }, {
      provide: HTTP_INTERCEPTORS,
      useClass: ProgressBarInterceptor,
      multi: true,
    }
  ],
  exports: [
    CommonModule,
    RouterModule,
    NavigationComponent,
    FooterComponent,
    CookiesComponent
  ]
})
export class CoreModule {

}
