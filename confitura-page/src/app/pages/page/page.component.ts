import {Component, OnInit, Input} from "@angular/core";
import {PageService} from "./page.service";
import {Page} from "./page.model";
@Component({
    selector: 'jl-page',
    template: '<div *ngIf="page" [innerHTML]="page.content"></div>'
})
export class PageComponent implements OnInit {
    @Input()
    title: string;

    page: Page;

    ngOnInit(): void {
        this.service.get(this.title)
            .subscribe(page => this.page = page);
    }

    constructor(private service: PageService) {

    }


}