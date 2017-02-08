import {Http, Response} from "@angular/http";
import {Partner} from "./partner.model";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
@Injectable()
export class PartnerService {
    constructor(private http: Http) {
    }

    getAll(): Observable<Map<String, Partner[]>> {
        return this.http.get(`http://c4p.confitura.pl/api/sponsors`)
            .map((response: Response) => response.json() as Map<String, Partner[]>);
    }

    getBy(name: string): Observable<Partner> {
        return this.http.get(`http://c4p.confitura.pl/api/sponsors/${name.toLowerCase()}`)
            .map((response: Response) => response.json() as Partner);
    }
}