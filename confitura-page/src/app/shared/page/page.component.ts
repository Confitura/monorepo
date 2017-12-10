import {Component, Input, OnInit} from '@angular/core';
import {Page} from './page.model';
import {PageService} from '../../core/page.service';

@Component({
  selector: 'cf-page',
  template: '<div *ngIf="page" [innerHTML]="page.content"></div>'
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
