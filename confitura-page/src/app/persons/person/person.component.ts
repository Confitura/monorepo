import {Component, Input, ElementRef, ViewChild} from "@angular/core";
import {Person} from "../../pages/about/person.model";
import {PersonModalService} from "../person-modal/person-modal.service";
@Component({
    selector: "jl-person",
    templateUrl: "./person.component.html"
})
export class PersonComponent {
    @Input()
    model: Person;
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
}