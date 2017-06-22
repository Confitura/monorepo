import {Component, Input} from "@angular/core";
import {MenuItem} from "./menu-item.model";
@Component({
    selector: "[cf-menu-item]",
    templateUrl: "./menu-item.component.html"
})
export class MenuItemComponent {
    @Input()
    model: MenuItem;

}