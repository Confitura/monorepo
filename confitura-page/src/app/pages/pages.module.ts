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
import {PartnerComponent} from "./partners/partner/partner.component";
import {RouterModule} from "@angular/router";
import {LoginComponent} from "./login/login.component";
import {FileUploadModule} from "ng2-file-upload";
import {PartnerBannerComponent} from "./partners/partner-banner/partner-banner.component";
@NgModule({
    imports: [SharedModule, PersonModule, RouterModule, FileUploadModule],
    declarations: [HomeComponent, NewsBannerComponent, NewsComponent, AboutComponent, PageComponent, PartnersComponent, PartnerComponent, LoginComponent, PartnerBannerComponent],
    providers: [NewsService, OrganizerService, PageService],
    exports: [HomeComponent, AboutComponent, PageComponent]
})
export class PagesModule {

}