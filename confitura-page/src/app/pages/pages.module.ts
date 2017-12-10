import {NgModule} from '@angular/core';
import {HomeComponent} from './home/home.component';
import {NewsBannerComponent} from './news/news-banner/news-banner.component';
import {NewsService} from './news/shared/news.service';
import {NewsComponent} from './news/news/news.component';
import {OrganizerService} from './about/organizer.service';
import {AboutComponent} from './about/about.component';
import {SharedModule} from '../shared/shared.module';
import {PersonModule} from '../persons/persons.module';
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

@NgModule({
  imports: [SharedModule, PersonModule, RouterModule, FileUploadModule],
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
    AgendaEntryComponent
  ],
  providers: [NewsService, OrganizerService],
  exports: [HomeComponent, AboutComponent]
})
export class PagesModule {

}
