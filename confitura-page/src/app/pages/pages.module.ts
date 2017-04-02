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
@NgModule({
    imports: [SharedModule, PersonModule, RouterModule],
    declarations: [HomeComponent, NewsBannerComponent, NewsComponent, AboutComponent, PageComponent, PartnersComponent, PartnerComponent, LoginComponent],
    providers: [NewsService, OrganizerService, PageService, PartnerService],
    exports: [HomeComponent, AboutComponent]
})
export class PagesModule {

}