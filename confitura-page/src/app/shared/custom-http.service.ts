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
        if (this.currentUser.isAvailable()) {
            if (options.headers == null) {
                options.headers = new Headers();
            }
            options.headers.set("Authorization", `Bearer ${this.currentUser.getToken()}`);
        }
        console.log(options.headers);
        return this.http.get(`${this.config.apiServer}${url}`, options);
    }

}