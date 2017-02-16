import {Http, Response} from "@angular/http";
import {Partner} from "./partner.model";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {HttpConfiguration} from "../../../shared/http-configuration.service";
@Injectable()
export class PartnerService {
    constructor(private http: Http, private configuration:HttpConfiguration) {
    }

    getAll(): Observable<Partner[]> {
        return this.http.get(`${this.configuration.apiServer}/partners`)

            .map((response: Response) => response.json()["_embedded"]["partners"] as Partner[]);
    }

    getBy(id: number): Observable<Partner> {
        return this.http.get(`${this.configuration.apiServer}/partners/${id}`)
            .map((response: Response) => response.json() as Partner);
    }
}