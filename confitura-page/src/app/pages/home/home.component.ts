import {Component, OnInit} from "@angular/core";
import {Http, URLSearchParams, Response} from "@angular/http";
import {ActivatedRoute, Params} from "@angular/router";
import Result = jasmine.Result;
import {User} from "./user.model";
@Component({
    templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {

    constructor(private http: Http, private route: ActivatedRoute) {

    }

    ngOnInit(): void {
        // this.route.queryParams
        //     .subscribe((params: Params) => {
        //         let searchParams = new URLSearchParams();
        //         searchParams.set("oauth_token", params["oauth_token"]);
        //         searchParams.set("oauth_verifier", params["oauth_verifier"]);
        //         this.http.get("http://localhost:9090/login/twitter/callback", {
        //             search: searchParams
        //         }).subscribe((response: Response) => {
        //             let token = response.text();
        //             let user:User = JSON.parse(atob(token.split(".")[1])) as User;
        //             user.token = token;
        //             // sessionStorage.setItem()
        //             console.log(user);
        //         })
        //
        //     });
    }

    login() {
        window.location.assign("http://localhost:9090/login/twitter");
        // this.http.get("http://localhost:9090/login/twitter").subscribe((response: Response) => {
        //     console.log(response.headers.get("location"));
        //
        // });
    }

}