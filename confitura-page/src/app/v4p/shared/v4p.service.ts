import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {Vote} from './vote.model';
import {Presentation} from '../../profile/shared/presentation.model';
import {HttpClient, HttpParams} from '@angular/common/http';

@Injectable()
export class V4pService {

  constructor(private http: HttpClient) {
  }

  start(token: string): Observable<Vote[]> {
    return this.http
      .post<EmbeddedVotes>(`/votes/start/${token}`, {})
      .pipe(
        map(response => response._embedded.votes)
      );
  }

  getPresentationFor(vote: Vote): Observable<Presentation> {
    const params = new HttpParams().set('projection', 'inlineSpeaker');
    return this.http.get<Presentation>(`/votes/${vote.id}/presentation`, {params});
  }

  save(vote: Vote) {
    return this.http.post(`/votes`, vote);
  }
}

class EmbeddedVotes {
  _embedded: { votes: Vote[] };
}
