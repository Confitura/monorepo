import {Component} from "@angular/core";
import {PersonModalService} from "./person-modal.service";
import "./person-modal.component.scss";
import {User} from "../../pages/profile/user.model";
@Component({
    selector: "jl-person-modal",
    templateUrl: "./person-modal.component.html"
})
export class PersonModalComponent {
    model: User;

    constructor(service: PersonModalService) {
        service.changed.subscribe((user: User) => {
            this.model = user;
            this.openModal();

        });
    }

    private openModal() {
        $(".modal").modal({show: true});
    }

}