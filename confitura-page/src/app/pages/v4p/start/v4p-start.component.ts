import {Component} from "@angular/core";
import {Router} from "@angular/router";
import {V4pService} from "../v4p.service";
import * as jsSHA from "jsSHA";
import "./v4p-start.component.scss";
@Component({
    templateUrl: "./v4p-start.component.html"
})
export class V4pStartComponent {
    constructor(private router: Router) {
        if (localStorage.getItem("v4p-token")) {
            this.router.navigate(["/v4p/voting"])
        }
    }

    start() {
        this.generateToken();
        this.router.navigate(["/v4p/voting"]);
    }

    private generateToken() {
        let sha = new jsSHA("SHA-256", "TEXT");
        sha.update(`${new Date().getMilliseconds()}${Math.random()}`);
        let token = sha.getHash("HEX");
        localStorage.setItem("v4p-token", token);
    }
}