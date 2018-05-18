import {Injectable} from '@angular/core';
import {Presentation} from './presentation.model';
import {Tag} from './tag.model';
import {Observable} from 'rxjs/Observable';
import {CurrentUser} from '../../core/security/current-user.service';
import {HttpClient, HttpParams} from '@angular/common/http';
import {map} from 'rxjs/operators';
import {User} from '../../core/user/user.model';

@Injectable()
export class PresentationService {
  constructor(private http: HttpClient,
              private user: CurrentUser) {
  }


  save(userId: string, presentation: Presentation): Observable<any> {
    return this.http.post<any>(`/users/${userId}/presentations`, presentation);
  }


  allTags() {
    return this.http.get<EmbeddedTags>('/tags')
      .pipe(
        map(response => response._embedded.tags)
      );
  }

  getAll(): Observable<Presentation[]> {
    let url = '/presentations';
    if (!this.user.isAdmin()) {
      url += '/search/accepted';
    }
    const params = new HttpParams().set('projection', 'inlineSpeaker');
    return this.http.get<EmbeddedPresentations>(url, {params})
      .pipe(map(response => response._embedded.presentations));
  }

  getAllFor(userId: string): Observable<Presentation[]> {
    return this.http.get<EmbeddedPresentations>(`/users/${userId}/presentations`)
      .pipe(map(response => response._embedded.presentations));
  }

  getOne(id: string): Observable<Presentation> {
    const params = new HttpParams().set('projection', 'inlineTags');
    return this.http.get<Presentation>(`/presentations/${id}`, {params});
  }

  remove(presentation: Presentation) {
    return this.http.delete(`/presentations/${presentation.id}`);
  }

  addCospeaker(id: string, email: string): Observable<User> {
    return this.http.post<User>(`/presentations/${id}/cospeakers/${email}`, {});
  }

  removeCospeaker(id: string, email: string): Observable<Response> {
    return this.http.delete<Response>(`/presentations/${id}/cospeakers/${email}`, {});
  }

  getCospeakers(id: string): Observable<User[]> {
    return this.http.get<EmbeddedUsers>(`/presentations/${id}/cospeakers`)
      .pipe(map(response => response._embedded.users));
  }

  accept(presentation: Presentation) {
    return this.http.post(`/presentations/${presentation.id}/accept`, {});
  }

  unaccept(presentation: Presentation) {
    return this.http.post(`/presentations/${presentation.id}/unaccept`, {});
  }
}

class EmbeddedTags {
  _embedded: { tags: Tag[] };
}

class EmbeddedUsers {
  _embedded: { users: User[] };
}

class EmbeddedPresentations {
  _embedded: { presentations: Presentation[] };
}

