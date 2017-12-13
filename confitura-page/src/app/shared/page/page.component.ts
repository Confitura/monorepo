import {Component, Input, OnInit} from '@angular/core';
import {Page} from './page.model';
import {PageService} from './page.service';

@Component({
  selector: 'cf-page',
  template: '<div *ngIf="page" [innerHTML]="page.content"></div>',
  providers: [PageService]
})

export class PageComponent implements OnInit {
  @Input()
  title: string;

  page: Page;

  constructor(private service: PageService) {

  }

  ngOnInit(): void {
    this.service.get(this.title)
      .subscribe(page => this.page = page);
  }

}
