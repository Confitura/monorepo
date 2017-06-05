import {UserService} from "../../../pages/profile/user.service";
import {Component, OnInit} from "@angular/core";
import {User} from "../../../pages/profile/user.model";
import {Observable} from "rxjs/Observable";
import {PersonModalService} from "../../../persons/person-modal/person-modal.service";
import {Router} from "@angular/router";
@Component({
    templateUrl: "./user-list.component.html"
})
export class UserListComponent implements OnInit {
    list: Observable<User[]>;

    constructor(private service: UserService, private modalService: PersonModalService, private router:Router) {

    }

    view(user: User) {
        this.modalService.showFor(user);
    }

    ngOnInit(): void {
        this.list = this.service.getAll();
    }

    addPresentationTo(user:User){
        this.router.navigate([`/user/${user.id}/presentation`]);
    }
}