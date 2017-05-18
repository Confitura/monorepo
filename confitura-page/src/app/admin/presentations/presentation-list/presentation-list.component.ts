import {AfterViewChecked, Component, OnInit} from "@angular/core";
import {PresentationService} from "../../../profile/shared/presentation.service";
import {Observable} from "rxjs/Observable";
import {Presentation} from "../../../profile/shared/presentation.model";
import "./presentation-list.component.scss";
import {PersonModalService} from "../../../persons/person-modal/person-modal.service";
import {User} from "../../../pages/profile/user.model";
import {UserService} from "../../../pages/profile/user.service";
import {ActivatedRoute} from "@angular/router";
@Component({
    templateUrl: "./presentation-list.component.html"
})
export class PresentationListComponent implements OnInit, AfterViewChecked {


    list: Observable<Presentation[]>;

    ngOnInit(): void {
        this.list = this.service.getAll();

    }

    ngAfterViewChecked(): void {
        let presentationId = this.route.snapshot.fragment;
        if (presentationId) {
            const element = document.getElementById(presentationId);
            if (element) {
                console.log(element.offsetTop);
                window.scrollTo(0, element.offsetTop - 100);
            }

        }
    }


    constructor(private service: PresentationService, private personModalService: PersonModalService, private userService: UserService, private route: ActivatedRoute) {
    }

    show(speaker: User) {
        this.userService.getBy(speaker.id, "withPresentations")
            .subscribe(user => this.personModalService.showFor(user));
    }

    clean(id: string) {
        return id.replace(/-/g, "")
    }


}