import {Http, Response, RequestOptionsArgs, Headers} from "@angular/http";
import {Injectable} from "@angular/core";
import {HttpConfiguration} from "./http-configuration.service";
import {Observable} from "rxjs";
import {CurrentUser} from "../security/current-user.service";
@Injectable()
export class CustomHttp {
    constructor(private config: HttpConfiguration,
                private currentUser: CurrentUser,
                private http: Http) {
    }

    get(url: string, options: RequestOptionsArgs = {}): Observable<Response> {
        this.addAuthorizationHeader(options);
        return this.http.get(this.toFullUrl(url), options);
    }



    post(url: string, body: any, options: RequestOptionsArgs = {}): Observable<Response> {
        this.addAuthorizationHeader(options);
        return this.http.post(this.toFullUrl(url), body, options);
    }

    private addAuthorizationHeader(options: RequestOptionsArgs) {
        if (this.currentUser.isAvailable()) {
            if (options.headers == null) {
                options.headers = new Headers();
            }
            options.headers.set("Authorization", `Bearer ${this.currentUser.getToken()}`);
        }
    }

    private toFullUrl(url: string) {
        return `${this.config.apiServer}${url}`;
    }



}