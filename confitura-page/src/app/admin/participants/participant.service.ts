import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import {Participant} from './participant.model';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {HttpConfiguration} from '../../shared/http-configuration.service';

@Injectable()
export class ParticipantService {
  private url: string;


  constructor(private http: HttpClient, private conf: HttpConfiguration) {
    this.url = `${conf.apiServer}/participant`;
  }

  getAll(): Observable<Participant[]> {
    return this.http.get<EmbeddedParticipants>(this.url)
      .map(response => response._embedded.participants);
  }


  getOne(id: string): Observable<Participant> {
    return this.http.get<Participant>(`${this.url}/${id}`);
  }

  save(participant: Participant) {
    return this.http.post(`${this.url}/${participant.id}`, participant);
  }

  sendReminder() {
    return this.http.post(`${this.url}/reminder`, {});
  }

  sendTickets() {
    return this.http.post(`${this.url}/ticket`, {});
  }

  sendSurveys() {
    return this.http.post(`${this.url}/survey`, {});
  }

  arrived(id: string): Observable<any> {
    return this.http.post<any>(`${this.url}/${id}/arrived`, {}, {observe: 'response'})
      .map((response: HttpResponse<any>) => ({status: response.status, json: response.body}));
  }
}

class EmbeddedParticipants {
  _embedded: { participants: Participant[] };
}
