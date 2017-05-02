import {Component, ElementRef, Input, ViewChild} from "@angular/core";
import {PersonModalService} from "../person-modal/person-modal.service";
import {User} from "../../pages/profile/user.model";
@Component({
    selector: "jl-person",
    templateUrl: "./person.component.html"
})
export class PersonComponent {
    @Input()
    model: User;
    @ViewChild("img")
    imgElement: ElementRef;

    constructor(private modalService: PersonModalService) {

    }


    showDefaultPhoto() {
        let element = this.imgElement.nativeElement;
        console.log($(element).get(0).onerror)
        // this.imgElement.error = null;
        // element.className +=" empty";
        // element.src = null;
    }

    openModal() {
        this.modalService.showFor(this.model);
    }

    firstName(): string {
        return this.model.name.split(" ")[0];
    }

    lastName(): string {
        return this.model.name.replace(this.firstName(), "").trim();
    }
}