import {Component, OnInit} from "@angular/core";
import {CurrentUser} from "../../security/current-user.service";
import {Router} from "@angular/router";
import {UserService} from "../../pages/profile/user.service";
import {User} from "../../pages/profile/user.model";

import "./profile-edit.component.scss";
@Component({
    templateUrl: "./profile-edit.component.html"
})
export class ProfileEditComponent implements OnInit {
    submitted = false;
    model: User = new User();

    constructor(private service: UserService, private currentUser: CurrentUser, private router: Router) {

    }

    ngOnInit(): void {
        if (this.currentUser.isAvailable()) {
            this.service.getBy(this.currentUser.get().jti)
                .subscribe(user => {
                    this.model = user
                });
        }
    }

    save() {
        this.submitted = true;
        this.service.save(this.model)
            .subscribe(response => this.router.navigate(["/profile"]));
    }

}