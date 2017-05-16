import {Component, OnInit} from "@angular/core";
import {PresentationService} from "../../../profile/shared/presentation.service";
import {Observable} from "rxjs/Observable";
import {Presentation} from "../../../profile/shared/presentation.model";
import "./presentation-list.component.scss";
@Component({
    templateUrl: "./presentation-list.component.html"
})
export class PresentationListComponent implements OnInit {
    list: Observable<Presentation[]>;

    ngOnInit(): void {
        this.list = this.service.getAll();
    }

    constructor(private service: PresentationService) {
    }


}