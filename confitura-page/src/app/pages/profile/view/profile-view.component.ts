import {Component, OnInit} from "@angular/core";
import {UserService} from "../user.service";
import {CurrentUser} from "../../../security/current-user.service";
import {User} from "../user.model";
import {HttpConfiguration} from "../../../shared/http-configuration.service";
@Component({
    templateUrl: "./profile-view.component.html"
})
export class ProfileViewComponent implements OnInit {

    model: User;


    constructor(private service: UserService, private currentUser: CurrentUser, config: HttpConfiguration) {

    }

    ngOnInit(): void {
        this.service.getBy(this.currentUser.get().jti)
            .subscribe(user => this.model = user);
    }

    reload() {
        this.service.getBy(this.currentUser.get().jti)
            .subscribe(user => {
                this.model = user;
                user.photo += "?v=" + new Date().getMilliseconds();
            });
    }


}