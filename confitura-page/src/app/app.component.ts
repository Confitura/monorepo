import {Component} from "@angular/core";
import "./app.component.scss";
import "./page.component.scss";

@Component({
    selector: 'cf-application',
    templateUrl: './app.component.html',
})
export class AppComponent {
    onDeactivate() {
        document.body.scrollTop = 0;
    }
}