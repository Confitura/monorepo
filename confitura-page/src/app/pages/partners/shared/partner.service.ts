import {Response} from "@angular/http";
import {Partner} from "./partner.model";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {CustomHttp} from "../../../shared/custom-http.service";
@Injectable()
export class PartnerService {
    constructor(private http: CustomHttp) {
    }

    getAll(): Observable<Partner[]> {
        return this.http.get("/partners")
            .map((response: Response) => response.json()["_embedded"]["partners"] as Partner[]);
    }

    getBy(id: string): Observable<Partner> {
        return this.http.get(`/partners/${id}`)
            .map((response: Response) => response.json() as Partner);
    }

    save(partner: Partner) {
        return this.http.post("/partners", partner);
    }

    delete(partner: Partner) {
        return this.http.remove(`/partners/${partner.id}`);
    }
}