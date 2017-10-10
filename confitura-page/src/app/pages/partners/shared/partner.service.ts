import {Response} from "@angular/http";
import {Partner} from "./partner.model";
import {Observable} from "rxjs/Observable";
import "rxjs/add/operator/map";
import {Injectable} from "@angular/core";
import {CustomHttp} from "../../../shared/custom-http.service";
import {CurrentUser} from "../../../security/current-user.service";

@Injectable()
export class PartnerService {
    constructor(private http: CustomHttp, private currentUser: CurrentUser) {
    }

    getAll(): Observable<Partner[]> {
        const url = this.currentUser.isAdmin() ? "/partners" : "/partners/search/published";
        return this.http.get(url)
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