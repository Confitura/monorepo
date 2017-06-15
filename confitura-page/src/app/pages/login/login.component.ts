import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {LoginService} from "../../security/login.service";
import {JwtUser} from "../home/jwt-user.model";

import "./login.scss";
import {HttpConfiguration} from "../../shared/http-configuration.service";
@Component({
    templateUrl: "./login.component.html"
})
export class LoginComponent implements OnInit {
    user: JwtUser;

    constructor(private route: ActivatedRoute,
                private router: Router,
                private login: LoginService,
                private config: HttpConfiguration) {
    }

    ngOnInit(): void {
        this.user = JSON.parse(sessionStorage.getItem("user")) as JwtUser;
        let origin = this.route.snapshot.params["origin"];
        let queryParams = this.route.snapshot.queryParams;
        if (origin == "twitter") {
            this.doLogin(() =>
                this.login.loginWithTwitter(queryParams["oauth_token"], queryParams["oauth_verifier"]));
        } else if (origin == "github") {
            this.doLogin(() => this.login.loginWithGitHub(queryParams["code"]));
        } else if (origin == "facebook") {
            this.doLogin(() => this.login.loginWithFacebook(queryParams["code"]));
        } else if (origin == "google") {
            debugger;
            this.doLogin(() => this.login.loginWithGoogle(queryParams["code"]));
        }
    }

    loginWith(origin: string) {
        window.location.assign(`${this.config.apiServer}/login/${origin}`);
    }


    private doLogin(callback: Function) {
        callback()
            .subscribe((user: JwtUser) => {
                this.user = user;
                if (user.isNew) {
                    this.router.navigate([`/profile/${user.jti}/edit`])
                } else {
                    this.router.navigate([`/profile/${user.jti}`])
                }
            });
    }

}