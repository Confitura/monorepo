import {Component, OnInit} from "@angular/core";
import {NewsService} from "../shared/news.service";
import {News} from "../shared/news.model";
import  "../shared/news.scss";
@Component({
    selector: "jl-news-banner",
    templateUrl: "./news-banner.component.html"
})
export class NewsBannerComponent implements OnInit {
    list: News[] = null;

    constructor(private service: NewsService) {
    }

    ngOnInit(): void {
        this.service.getAll(0, 3)
            .subscribe((list: News[]) => this.list = list);
    }

}