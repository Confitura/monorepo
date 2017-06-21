import {Component} from "@angular/core";
import {UserService} from "../profile/user.service";
import {Observable} from "rxjs/Observable";
import {User} from "../profile/user.model";

import "./speaker-list.component.scss";

@Component({
    templateUrl: "./speaker-list.component.html"
})
export class SpeakerListComponent {
    list: Observable<User[]>;

    constructor(private service: UserService) {
        this.list = this.service.getAllSpeakers();
    }
}