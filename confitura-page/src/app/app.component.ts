import {Component} from "@angular/core";
// import * as styles from "./app.component.scss";
import "./page.component.scss";
import "./app.component.scss";

@Component({
    selector: 'cf-application',
    templateUrl: './app.component.html',
    // styleUrls: ['./app.component.scss', './page.component.scss']

})
export class AppComponent {
    onDeactivate() {
        document.body.scrollTop = 0;
    }
}