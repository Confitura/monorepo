import {Component, OnInit} from "@angular/core";
import "./login.scss";
import {ActivatedRoute, Params} from "@angular/router";
import {User} from "../home/user.model";
import {Location} from "@angular/common";
import {LoginService} from "../../security/login.service";
import {CurrentUser} from "../../security/current-user.service";
@Component({
    templateUrl: "./login.component.html"
})
export class LoginComponent implements OnInit {
    user: User;

    constructor(private route: ActivatedRoute,
                private location: Location,
                private login: LoginService,
                private currentUser: CurrentUser) {
    }

    ngOnInit(): void {
        this.user = JSON.parse(sessionStorage.getItem("user")) as User;
        this.route.queryParams
            .subscribe((params: Params) => {
                let token = params["oauth_token"];
                let verifier = params["oauth_verifier"];
                if (token && verifier) {
                    this.login.login(token, verifier)
                        .subscribe((user: User) => {
                            this.user = user;
                            this.location.replaceState("login");
                        });
                }
            });
    }

    twitterLogin() {
        window.location.assign("http://localhost:9090/login/twitter");
    }

}