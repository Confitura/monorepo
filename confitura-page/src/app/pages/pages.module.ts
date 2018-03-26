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
import {AgendaEntryComponent} from './agenda/agenda-entry/agenda-entry.component';
import {AgendaEntryModalComponent} from './agenda/entry-modal/entry-modal.component';
import {LoginComponent} from './login/login.component';
import {AgmCoreModule} from '@agm/core';
import {FaqComponent} from './faq/faq.component';
import {TagInputModule} from 'ngx-chips';
import {
  MatButtonModule, MatChipsModule, MatFormFieldModule, MatIconModule, MatInputModule, MatMenuModule, MatRadioModule,
  MatTooltipModule
} from '@angular/material';

@NgModule({
  imports: [
    SharedModule,
    RouterModule,
    FileUploadModule,
    TagInputModule,
    MatIconModule,
    MatTooltipModule,
    MatChipsModule,
    MatButtonModule,
    MatMenuModule,
    MatRadioModule,
    MatFormFieldModule,
    MatInputModule,
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
    AgendaEntryComponent,
    FaqComponent
  ],
  providers: [NewsService, OrganizerService],
  exports: [HomeComponent, AboutComponent]
})
export class PagesModule {

}
