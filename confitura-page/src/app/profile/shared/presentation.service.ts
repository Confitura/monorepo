import {Injectable} from '@angular/core';
import {Presentation} from './presentation.model';
import {Response} from '@angular/http';
import {Tag} from './tag.model';
import {Observable} from 'rxjs/Observable';
import {CurrentUser} from '../../security/current-user.service';
import {User} from '../../pages/profile/user.model';
import {HttpClient, HttpParams} from '@angular/common/http';

@Injectable()
export class PresentationService {
  constructor(private http: HttpClient,
              private user: CurrentUser) {
  }


  save(userId: string, presentation: Presentation): Observable<Response> {
    return this.http.post(`/users/${userId}/presentations`, presentation);
  }


  allTags() {
    return this.http.get<EmbeddedTags>('/tags')
      .map(response => response._embedded.tags);
  }

  getAll(): Observable<Presentation[]> {
    let url = '/presentations';
    if (!this.user.isAdmin()) {
      url += '/search/accepted';
    }
    const params = new HttpParams().set('projection', 'inlineSpeaker');
    return this.http.get<EmbeddedPresentations>(url, {params})
      .map(response => response._embedded.presentations);

  }

  getAllFor(userId: string): Observable<Presentation[]> {
    return this.http.get<EmbeddedPresentations>(`/users/${userId}/presentations`)
      .map(response => response._embedded.presentations);
  }

  getOne(id: string): Observable<Presentation> {
    const params = new HttpParams().set('projection', 'inlineTags');
    return this.http.get<Presentation>(`/presentations/${id}`, {params});
  }

  remove(presentation: Presentation) {
    return this.http.delete(`/presentations/${presentation.id}`);
  }

  getCospeakers(id: string): Observable<User[]> {
    return this.http.get<EmbeddedUsers>(`/presentations/${id}/cospeakers`)
      .map(response => response._embedded.users);
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

