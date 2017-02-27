import {Component, OnInit} from "@angular/core";
import {Http} from "@angular/http";
import {ActivatedRoute} from "@angular/router";
import Result = jasmine.Result;
@Component({
    templateUrl: './home.component.html'
})
export class HomeComponent {

    constructor(private http: Http, private route: ActivatedRoute) {

    }


}