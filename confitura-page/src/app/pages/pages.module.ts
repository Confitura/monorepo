import {NgModule} from "@angular/core";
import {HomeComponent} from "./home/home.component";
import {NewsBannerComponent} from "./news/news-banner/news-banner.component";
import {NewsService} from "./news/shared/news.service";
import {NewsComponent} from "./news/news/news.component";
import {OrganizerService} from "./about/organizer.service";
import {AboutComponent} from "./about/about.component";
import {PageComponent} from "./page/page.component";
import {PageService} from "./page/page.service";
import {SharedModule} from "../shared/shared.module";
import {PersonModule} from "../persons/persons.module";
import {PartnersComponent} from "./partners/partner-list/partners.component";
import {PartnerService} from "./partners/shared/partner.service";
import {PartnerComponent} from "./partners/partner/partner.component";
import {RouterModule} from "@angular/router";
import {LoginComponent} from "./login/login.component";
import {PresentationComponent} from "./profile/presentation/presentation.component";
import {TagInputModule} from "ng2-tag-input";
import {PresentationService} from "./profile/presentation/presentation.service";
@NgModule({
    imports: [SharedModule, PersonModule, RouterModule, TagInputModule],
    declarations: [HomeComponent, NewsBannerComponent, NewsComponent, AboutComponent, PageComponent, PartnersComponent, PartnerComponent, LoginComponent, PresentationComponent],
    providers: [NewsService, OrganizerService, PageService, PartnerService, PresentationService],
    exports: [HomeComponent, AboutComponent]
})
export class PagesModule {

}