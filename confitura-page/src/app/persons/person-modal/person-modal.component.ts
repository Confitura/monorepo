import {Component} from "@angular/core";
import {PersonModalService} from "./person-modal.service";
import "./person-modal.component.scss";
import {User} from "../../pages/profile/user.model";
import {Presentation} from "../../profile/shared/presentation.model";
import {Router} from "@angular/router";
@Component({
    selector: "jl-person-modal",
    templateUrl: "./person-modal.component.html"
})
export class PersonModalComponent {
    model: User;

    constructor(service: PersonModalService, private router: Router) {
        service.changed.subscribe((user: User) => {
            this.model = user;
            this.openModal();
        });
    }

    goTo(presentation: Presentation) {
        $(".modal").modal("hide");
        this.router.navigate(["/admin2/presentations"], {fragment: presentation.id})
    }

    private openModal() {
        $(".modal").modal({show: true});
    }


}