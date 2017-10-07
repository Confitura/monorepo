import {Injectable} from "@angular/core";
import {CustomHttp} from "../../shared/custom-http.service";
import {Observable} from "rxjs/Observable";
import "rxjs/add/operator/map"
import {Vote} from "./vote.model";
import {Response} from "@angular/http";
import {Presentation} from "../../profile/shared/presentation.model";
@Injectable()
export class V4pService {
    constructor(private http: CustomHttp) {

    }

    start(token: string): Observable<Vote[]> {
        return this.http.post(`/votes/start/${token}`, {})
            .map((response: Response) => response.json()["_embedded"]["votes"] as Vote[])
    }

    getPresentationFor(vote:Vote): Observable<Presentation> {
        return this.http.get(`/votes/${vote.id}/presentation?projection=inlineSpeaker`)
            .map((response: Response) => response.json() as Presentation)
    }

    save(vote:Vote){
        return this.http.post(`/votes`, vote);
    }
}