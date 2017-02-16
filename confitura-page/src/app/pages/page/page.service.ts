import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Page} from "./page.model";
import {Observable} from "rxjs";
import {HttpConfiguration} from "../../shared/http-configuration.service";
@Injectable()
export class PageService {

    constructor(private http: Http, private configuration:HttpConfiguration) {

    }

    get(name: string): Observable<Page> {
        console.log(name);
        return this.http.get(`${this.configuration.apiServer}/pages/${name}`)
            .map((response: Response) => response.json() as Page);
    }


}