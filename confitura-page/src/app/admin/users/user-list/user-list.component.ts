import {UserService} from "../../../pages/profile/user.service";
import {Component, OnInit} from "@angular/core";
import {User} from "../../../pages/profile/user.model";
import {Observable} from "rxjs/Observable";
import {PersonModalService} from "../../../persons/person-modal/person-modal.service";
@Component({
    templateUrl: "./user-list.component.html"
})
export class UserListComponent implements OnInit {
    list: Observable<User[]>;

    ngOnInit(): void {
        this.list = this.service.getAll();

    }

    constructor(private service: UserService, private modalService: PersonModalService) {

    }

    view(user: User) {
        this.modalService.showFor(user);
    }
}