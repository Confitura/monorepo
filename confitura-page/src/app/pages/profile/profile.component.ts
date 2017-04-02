import {Component, OnInit} from "@angular/core";
import "./profile.component.scss";
import {UserService} from "./user.service";
import {CurrentUser} from "../../security/current-user.service";
import {User} from "./user.model";
import {Router} from "@angular/router";
@Component({
    templateUrl: "./profile.component.html"
})
export class ProfileComponent implements OnInit {
    model: User = new User();

    constructor(private service: UserService, private currentUser: CurrentUser, private router:Router) {

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
        console.log("saving");
        this.service.save(this.model)
            .subscribe(response => {
                this.router.navigate(["/my-profile"]);
            })
    }

}