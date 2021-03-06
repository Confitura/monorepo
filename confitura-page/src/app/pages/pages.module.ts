import {NgModule} from '@angular/core';
import {HomeComponent} from './home/home.component';
import {NewsBannerComponent} from './news/news-banner/news-banner.component';
import {NewsService} from './news/shared/news.service';
import {NewsComponent} from './news/news/news.component';
import {OrganizerService} from './about/organizer.service';
import {AboutComponent} from './about/about.component';
import {SharedModule} from '../shared/shared.module';
import {PartnersComponent} from './partners/partner-list/partners.component';
import {PartnerComponent} from './partners/partner/partner.component';
import {RouterModule} from '@angular/router';
import {FileUploadModule} from 'ng2-file-upload';
import {PartnerBannerComponent} from './partners/partner-banner/partner-banner.component';
import {PresentationListComponent} from './presentations/presentation-list/presentation-list.component';
import {SpeakerListComponent} from './speakers/speaker-list.component';
import {AgendaComponent} from './agenda/agenda.component';
import {AgendaEntryDisplayComponent} from './agenda/agenda-entry/agenda-entry-display.component';
import {AgendaEntryModalComponent} from './agenda/entry-modal/entry-modal.component';
import {LoginComponent} from './login/login.component';
import {AgmCoreModule} from '@agm/core';
import {FaqComponent} from './faq/faq.component';
import {TagInputModule} from 'ngx-chips';
import {WorkshopsComponent} from './workshops/workshops.component';
import {PrivacyPolicyComponent} from './privacy-policy/privacy-policy.component';
import {AgendaFilterComponent} from './agenda/agenda-filter/agenda-filter.component';
import {AgendaTableComponent} from './agenda/agenda-table/agenda-table.component';
import {LiveStreamComponent} from './live-stream/live-stream.component';
import {PersonalAgendaComponent} from './agenda/personal-agenda/personal-agenda.component';
import {SpoinaComponent} from './spoina/spoina.component';

@NgModule({
  imports: [
    SharedModule,
    RouterModule,
    FileUploadModule,
    TagInputModule,
    AgmCoreModule.forRoot({
      apiKey: 'AIzaSyA6nOtsqyZJRfMwy6kmaK2_MgX51TtmViA'
    })],
  declarations: [
    HomeComponent,
    NewsBannerComponent,
    NewsComponent,
    AboutComponent,
    PartnersComponent,
    PartnerComponent,
    LoginComponent,
    PartnerBannerComponent,
    PresentationListComponent,
    SpeakerListComponent,
    AgendaComponent,
    AgendaEntryModalComponent,
    AgendaEntryDisplayComponent,
    FaqComponent,
    SpoinaComponent,
    WorkshopsComponent,
    PrivacyPolicyComponent,
    AgendaFilterComponent,
    AgendaTableComponent,
    LiveStreamComponent,
    PersonalAgendaComponent
  ],
  providers: [NewsService, OrganizerService],
  exports: [HomeComponent, AboutComponent]
})
export class PagesModule {

}
