import {Component} from "@angular/core";
import "./registration-info.component.scss";
import {ActivatedRoute} from "@angular/router";
@Component({
    templateUrl: "./registration-info.component.html"
})
export class RegistrationInfoComponent {
    name: string;


    constructor(route: ActivatedRoute) {
        this.name = route.snapshot.params["name"] || "registration-info";
    }
}