import {NgModule} from "@angular/core";
import {HomeComponent} from "./home/home.component";
import {NewsBannerComponent} from "./news/news-banner/news-banner.component";
import {NewsService} from "./news/shared/news.service";
import {HttpModule} from "@angular/http";
import {BrowserModule} from "@angular/platform-browser";
import {NewsComponent} from "./news/news/news.component";
@NgModule({
    imports: [HttpModule, BrowserModule],
    declarations: [HomeComponent, NewsBannerComponent, NewsComponent],
    providers: [NewsService],
    exports: [HomeComponent]
})
export class PagesModule{

}