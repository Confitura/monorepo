import {Component, OnInit} from '@angular/core';
import {NewsService} from '../shared/news.service';
import {News} from '../shared/news.model';

@Component({
  selector: 'cf-news-banner',
  templateUrl: './news-banner.component.html',
  styleUrls: ['../shared/news.component.scss']

})
export class NewsBannerComponent implements OnInit {
  list: News[] = [];

  constructor(private service: NewsService) {
  }

  ngOnInit(): void {
    this.service.getPage(0, 3)
      .subscribe((list: News[]) => this.list = list);
  }

}
