import {Injectable} from "@angular/core";
import {Response, URLSearchParams} from "@angular/http";
import {CustomHttp} from "../shared/custom-http.service";
import {CurrentUser} from "./current-user.service";
import {Observable} from "rxjs/Observable";
import {Observer} from "rxjs/Observer";
import {JwtUser} from "../pages/home/jwt-user.model";
@Injectable()
export class LoginService {

    constructor(private http: CustomHttp, private currentUser: CurrentUser) {
    }

    loginWithTwitter(token: string, verifier: string): Observable<JwtUser> {
        return this.doLogin("twitter", new URLSearchParams(`oauth_token=${token}&oauth_verifier=${verifier}`));
    }


    loginWithGitHub(code: string): Observable<JwtUser> {
        return this.doLogin("github", new URLSearchParams(`code=${code}`));
    }

    loginWithFacebook(code: string): Observable<JwtUser> {
        return this.doLogin("facebook", new URLSearchParams(`code=${code}`));
    }

    loginWithGoogle(code: string): Observable<JwtUser> {
        return this.doLogin("google", new URLSearchParams(`code=${code}`));
    }


    logout() {
        this.currentUser.logout();
    }

    private doLogin(system: string, searchParams: URLSearchParams) {
        return Observable.create((observer: Observer<JwtUser>) => {
            this.http.get(`/login/${system}/callback`, {
                search: searchParams
            }).subscribe((response: Response) => {
                this.currentUser.set(response.text());
                observer.next(this.currentUser.get());
            });
        });
    }
}