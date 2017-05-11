import {Component, OnInit} from "@angular/core";
import {OrganizerService} from "./organizer.service";
import {Observable} from "rxjs";
import {Person} from "./person.model";
import "./about.scss";
import {PersonModalService} from "../../persons/person-modal/person-modal.service";
import {User} from "../profile/user.model";
@Component({
    templateUrl: "./about.component.html",
})
export class AboutComponent implements OnInit {
    organizers: Observable<Person[]> = null;
    volunteers: Observable<Person[]> = null;

    constructor(private service: OrganizerService, private personModalService:PersonModalService) {

    }

    ngOnInit(): void {
        this.organizers = this.service.getAllOrganizers();
        this.volunteers = this.service.getAllVolunteers();
    }

    show(user:User){
        this.personModalService.showFor(user);
    }


}