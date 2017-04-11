import {Component, OnInit} from "@angular/core";
import {CurrentUser} from "../../security/current-user.service";
import {LoginService} from "../../security/login.service";
import {MenuItem} from "./menu-item.model";
import "./navigation.component.scss";
import {User} from "../../pages/profile/user.model";
@Component({
    selector: "cf-navigation",
    templateUrl: "./navigation.component.html"
})
export class NavigationComponent implements OnInit {
    private loggedIn: boolean;
    menu: MenuItem[] = [
        {label: "about us", url: "/about"},
        {label: "partners", url: "/partners"},
        {label: "profile", url: "/profile", show: () => this.loggedIn},
        {label: "login", url: "/login", clazz: "pink", show: () => !this.loggedIn},
        {label: "logout", action: () => this.logout(), clazz: "pink", show: () => this.loggedIn},
    ];


    constructor(private currentUser: CurrentUser, private login: LoginService) {
    }

    ngOnInit(): void {
        this.loggedIn = this.currentUser.isAvailable();
        this.currentUser
            .onLogin.subscribe((user: User) => this.loggedIn = this.currentUser.isAvailable());
    }

    logout() {
        this.login.logout();
        this.closeMenu();
    };

    closeMenu() {
        // $(".navbar-toggle").click();
    }


}