import {Injectable} from "@angular/core";
import {Response, URLSearchParams} from "@angular/http";
import {News} from "./news.model";
import {Observable} from "rxjs";
import {CustomHttp} from "../../../shared/custom-http.service";

@Injectable()
export class NewsService {
    constructor(private http: CustomHttp) {
    }

    getPage(page: number, size: number): Observable<News[]> {
        const params = new URLSearchParams();
        params.set("page", `${page}`);
        params.set("size", `${size}`);
        params.set("sort", "creationDate,desc");

        return this.http.get(`/news/search/published`, {search: params})
            .map((response: Response) => {
                return response.json()['_embedded']['news'] as News[]
            });
    }
}