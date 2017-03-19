import {Component, OnInit} from "@angular/core";
import {CurrentUser} from "../security/current-user.service";
import {LoginService} from "../security/login.service";
import {User} from "../pages/profile/user.model";
@Component({
    selector: "cf-navigation",
    templateUrl: "./navigation.component.html"
})
export class NavigationComponent implements OnInit {
    private loggedIn: boolean;

    constructor(private currentUser: CurrentUser, private login:LoginService) {
    }

    ngOnInit(): void {
        this.loggedIn = this.currentUser.isAvailable();
        this.currentUser.onLogin.subscribe((user:User) => this.loggedIn = this.currentUser.isAvailable());
    }

    logout(){
        this.login.logout();
    }


}