import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Params, Router} from "@angular/router";
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
        this.route.queryParams
            .subscribe((params: Params) => {
                let token = params["oauth_token"];
                let verifier = params["oauth_verifier"];
                if (token && verifier) {
                    this.login.login(token, verifier)
                        .subscribe((user: JwtUser) => {
                            this.user = user;
                            if (user.isNew) {
                                this.router.navigate(["/profile/edit"])
                            } else {
                                this.router.navigate(["/profile"])
                            }
                        });
                }
            });
    }

    twitterLogin() {
        window.location.assign(`${this.config.apiServer}/login/twitter`);
    }

}