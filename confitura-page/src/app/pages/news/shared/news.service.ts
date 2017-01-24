import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {News} from "./news.model";
import {Observable} from "rxjs";

@Injectable()
export class NewsService {
    constructor(private http: Http) {
    }

    getAll(page:number, size:number): Observable<News[]> {
        return this.http.get(`http://c4p.confitura.pl/api/news/${page}/${size}`)
            .map((response: Response) => {
                return response.json() as News[]
            });
    }
}