import {Response} from "@angular/http";
import {Partner} from "./partner.model";
import {Observable} from "rxjs/Observable";
import "rxjs/add/operator/map";
import "rxjs/add/operator/switchMap";
import "rxjs/add/operator/reduce";
import {Injectable} from "@angular/core";
import {CustomHttp} from "../../../shared/custom-http.service";
import {CurrentUser} from "../../../security/current-user.service";
import {ImageResizer} from "../../../shared/ImageResizer.service";

@Injectable()
export class PartnerService {
    constructor(private http: CustomHttp,
                private currentUser: CurrentUser,
                private resizer:ImageResizer) {
    }

    getAll(): Observable<Partner[]> {
        const url = this.currentUser.isAdmin() ? "/partners" : "/partners/search/published";
        return this.http.get(url)
            .switchMap((response: Response) => response.json()["_embedded"]["partners"] as Partner[])
            .map(partner => ({...partner, logo: this.resizer.applyResizing(partner.logo)}))
            .reduce((list, partner) => [...list, partner], []);
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