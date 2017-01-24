import {Component} from "@angular/core";
import "./cookies.component.scss";
import {Cookie} from "ng2-cookies/ng2-cookies";
@Component({
    selector: 'cf-cookies',
    templateUrl: './cookies.component.html',

})
export class CookiesComponent {

    isNotAccepted(){
        return Cookie.get("cookies-accepted") !== "true" || false;
    }

    accept(){
        Cookie.set("cookies-accepted", "true");

    }

}