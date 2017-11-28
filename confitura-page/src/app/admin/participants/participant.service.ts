import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import {Participant} from './participant.model';
import {HttpClient, HttpResponse} from '@angular/common/http';

@Injectable()
export class ParticipantService {
  constructor(private http: HttpClient) {
  }

  getAll(): Observable<Participant[]> {
    return this.http.get<EmbeddedParticipants>('/participants')
      .map(response => response._embedded.participants);
  }


  getOne(id: string): Observable<Participant> {
    return this.http.get<Participant>(`/participants/${id}`);
  }

  save(participant: Participant) {
    return this.http.post(`participants/${participant.id}`, participant);
  }

  sendReminder() {
    return this.http.post(`/participants/reminder`, {});
  }

  sendTickets() {
    return this.http.post(`/participants/ticket`, {});
  }

  sendSurveys() {
    return this.http.post(`/participants/survey`, {});
  }

  arrived(id: string): Observable<any> {
    return this.http.post<any>(`/participants/${id}/arrived`, {}, {observe: 'response'})
      .map((response: HttpResponse<any>) => ({status: response.status, json: response.body}));
  }
}

class EmbeddedParticipants {
  _embedded: { participants: Participant[] };
}
