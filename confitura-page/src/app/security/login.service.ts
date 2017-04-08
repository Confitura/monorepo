import {Injectable} from "@angular/core";
import {Response, URLSearchParams} from "@angular/http";
import {CustomHttp} from "../shared/custom-http.service";
import {CurrentUser} from "./current-user.service";
import {Observable, Observer} from "rxjs";
import {JwtUser} from "../pages/home/jwt-user.model";
@Injectable()
export class LoginService {

    constructor(private http: CustomHttp, private currentUser: CurrentUser) {
    }

    login(token: string, verifier: string): Observable<JwtUser> {
        return Observable.create((observer: Observer<JwtUser>) => {
            let searchParams = new URLSearchParams();
            searchParams.set("oauth_token", token);
            searchParams.set("oauth_verifier", verifier);
            this.http.get("/login/twitter/callback", {
                search: searchParams
            })
                .subscribe((response: Response) => {
                    this.currentUser.set(response.text());
                    observer.next(this.currentUser.get());
                });
        });
    }


    logout() {
        this.currentUser.logout();
    }
}