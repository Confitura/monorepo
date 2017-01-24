import {Component, Input} from "@angular/core";
import {News} from "../shared/news.model";
@Component({
    selector: "jl-news",
    templateUrl: "./news.component.html"
})
export class NewsComponent{
    @Input()
    model:News;

}