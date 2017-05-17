import {Component, OnInit} from "@angular/core";
import {PresentationService} from "../../../profile/shared/presentation.service";
import {Observable} from "rxjs/Observable";
import {Presentation} from "../../../profile/shared/presentation.model";
import "./presentation-list.component.scss";
import {PersonModalService} from "../../../persons/person-modal/person-modal.service";
import {User} from "../../../pages/profile/user.model";
import {UserService} from "../../../pages/profile/user.service";
@Component({
    templateUrl: "./presentation-list.component.html"
})
export class PresentationListComponent implements OnInit {
    list: Observable<Presentation[]>;

    ngOnInit(): void {
        this.list = this.service.getAll();
    }

    constructor(private service: PresentationService, private personModalService: PersonModalService, private userService: UserService) {
    }

    show(speaker: User) {
        this.userService.getBy(speaker.id, "withPresentations")
            .subscribe(user => this.personModalService.showFor(user));
    }


}