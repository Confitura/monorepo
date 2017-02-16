import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {News} from "./news.model";
import {Observable} from "rxjs";
import {HttpConfiguration} from "../../../shared/http-configuration.service";

@Injectable()
export class NewsService {
    constructor(private http: Http, private configuration: HttpConfiguration) {
    }

    getAll(page: number, size: number): Observable<News[]> {
        return this.http.get(`${this.configuration.apiServer}news/search/published?page=0&size=3&sort=creationDate,desc`)
            .map((response: Response) => {
                return response.json()['_embedded']['news'] as News[]
            });
    }
}