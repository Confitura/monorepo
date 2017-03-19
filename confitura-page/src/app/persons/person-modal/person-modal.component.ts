import {Component, OnInit} from "@angular/core";
import {PersonModalService} from "./person-modal.service";
import {Person} from "../../pages/about/person.model";
import "./person-modal.component.scss";
@Component({
    selector: "jl-person-modal",
    templateUrl: "./person-modal.component.html"
})
export class PersonModalComponent implements OnInit {
    private person: Person;

    ngOnInit(): void {
    }

    constructor(service: PersonModalService) {
        service.changed.subscribe((person:Person) => {
            this.person = person;
            this.openModal();

        });
    }

    private openModal() {
        $(".modal").modal({show: true});
    }

}