import {Injectable, OnInit} from "@angular/core";
import {URLSearchParams, Response} from "@angular/http";
import {CustomHttp} from "../shared/custom-http.service";
import {CurrentUser} from "./current-user.service";
import {Observable, Observer} from "rxjs";
import {User} from "../pages/home/user.model";
@Injectable()
export class LoginService  {

    constructor(private http: CustomHttp, private currentUser: CurrentUser) {
    }

    login(token: string, verifier: string): Observable<User> {
        return Observable.create((observer: Observer<User>) => {
            let searchParams = new URLSearchParams();
            searchParams.set("oauth_token", token);
            searchParams.set("oauth_verifier", verifier);
            this.http.get("/login/twitter/callback", {
                search: searchParams
            }).subscribe((response: Response) => {
                console.log("User got", response.text());
                this.currentUser.set(response.text());
                observer.next(this.currentUser.get());
            });
        });
    }


    logout() {
        this.currentUser.logout();
    }
}