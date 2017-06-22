import {Component, OnInit} from "@angular/core";
import {CurrentUser} from "../../security/current-user.service";
import {LoginService} from "../../security/login.service";
import {MenuItem} from "./menu-item.model";
import "./navigation.component.scss";
import {User} from "../../pages/profile/user.model";
import {Router} from "@angular/router";
@Component({
    selector: "cf-navigation",
    templateUrl: "./navigation.component.html",
})
export class NavigationComponent implements OnInit {
    private loggedIn: boolean;
    menu: MenuItem[] = [
        {label: "about us", url: "/about"},
        {label: "partners", url: "/partners"},
        {label: "schedule", url: "/schedule"},
        {label: "presentations", url: "/presentations"},
        {label: "speakers", url: "/speakers"},
        {label: "profile", action: () => this.goToProfile(), show: () => this.loggedIn},
        {
            label: "admin", show: () => this.currentUser.isAdmin(),
            children: [
                {label: "participants", url: "/admin2/participants"},
                {label: "manage schedule", url: "/admin2/agenda"},
                {label: "users", url: "/admin2/users"},
            ]
        },
        {label: "login", url: "/login", show: () => !this.loggedIn},
        {label: "logout", action: () => this.logout(), show: () => this.loggedIn},
        {label: "registration", url: "/registration", clazz: "pink"},
    ];


    constructor(private currentUser: CurrentUser,
                private login: LoginService,
                private router: Router) {
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

    goToProfile() {
        this.router.navigate([`/profile/${this.currentUser.get().jti}`]);

    }

    closeMenu() {
        $("#navbar").collapse('hide');

    }


}
