import {NgModule} from "@angular/core";
import {HomeComponent} from "./home/home.component";
import {NewsBannerComponent} from "./news/news-banner/news-banner.component";
import {NewsService} from "./news/shared/news.service";
import {HttpModule} from "@angular/http";
import {BrowserModule} from "@angular/platform-browser";
import {NewsComponent} from "./news/news/news.component";
import {OrganizerService} from "./about/organizer.service";
import {AboutComponent} from "./about/about.component";
import {PageComponent} from "./page/page.component";
import {PageService} from "./page/page.service";
import {PersonModalComponent} from "../components/person-modal/person-modal.component";
import {PersonModalService} from "../components/person-modal/person-modal.service";
import {PersonComponent} from "../components/person/person.component";
@NgModule({
    imports: [HttpModule, BrowserModule],
    declarations: [HomeComponent, NewsBannerComponent, NewsComponent, AboutComponent, PageComponent, PersonModalComponent, PersonComponent],
    providers: [NewsService, OrganizerService, PageService, PersonModalService],
    exports: [HomeComponent, AboutComponent]
})
export class PagesModule{

}