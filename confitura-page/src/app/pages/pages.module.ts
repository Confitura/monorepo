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
@NgModule({
    imports: [SharedModule, PersonModule],
    declarations: [HomeComponent, NewsBannerComponent, NewsComponent, AboutComponent, PageComponent],
    providers: [NewsService, OrganizerService, PageService],
    exports: [HomeComponent, AboutComponent]
})
export class PagesModule {

}