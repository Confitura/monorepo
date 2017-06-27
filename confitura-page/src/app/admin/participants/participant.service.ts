import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import {Participant} from "./participant.model";
import {CustomHttp} from "../../shared/custom-http.service";
import {Response} from "@angular/http";
@Injectable()
export class ParticipantService {


    constructor(private http: CustomHttp) {
    }

    getAll(): Observable<Participant[]> {
        return this.http.get('/participants')
            .map((response: Response) => response.json()["_embedded"]["participants"] as Participant[]);
    }


    getOne(id: string): Observable<Participant> {
        return this.http.get(`/participants/${id}`)
            .map((response: Response) => response.json() as Participant);
    }

    save(participant: Participant) {
        return this.http.post(`/participants/${participant.id}`, participant);
    }

    sendReminder() {
        return this.http.post(`/participants/reminder`, {});
    }

    sendTickets() {
        return this.http.post(`/participants/ticket`, {});
    }

    arrived(id: string): Observable<any> {
        return this.http.post(`/participants/${id}/arrived`, {})
            .map((response: Response) => {
                return {status: response.status, json: response.json()};
            });
    }
}