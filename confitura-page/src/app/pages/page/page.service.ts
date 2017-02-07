import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Page} from "./page.model";
import {Observable} from "rxjs";
@Injectable()
export class PageService {

    constructor(private http: Http) {

    }

    get(name: string): Observable<Page> {
        console.log(name);
        return this.http.get(`http://c4p.confitura.pl/api/pages/${name}`)
            .map((response: Response) => response.json() as Page);
    }


}