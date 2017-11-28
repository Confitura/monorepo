import {Injectable} from '@angular/core';
import {Response} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import {User} from './user.model';
import 'rxjs/add/operator/map';
import {HttpClient, HttpParams} from '@angular/common/http';

@Injectable()
export class UserService {
  constructor(private http: HttpClient) {
  }

  getBy(id: string, projection: string = null): Observable<User> {
    const params = new HttpParams();
    if (projection) {
      params.set('projection', projection);
    }
    return this.http.get<User>(`/users/${id}`, {params});
  }

  save(user: User) {
    return this.http.post(`/users`, user);
  }

  getAll(): Observable<User[]> {
    return this.http.get<EmbeddedUsers>(`/users`)
      .map(response => response._embedded.users);
  }

  getAllSpeakers(): Observable<User[]> {
    return this.http.get<EmbeddedUsers>(`/users/search/speakers`)
      .map(response => response._embedded.users);

  }

  markAsVolunteer(user: User, isVolunteer: boolean): Observable<Response> {
    return this.http.post(`/users/${user.id}/volunteer/${isVolunteer}`, {});
  }


  find(query: string): Observable<User[]> {
    const params = new HttpParams().set('query', query);
    return this.http.get<EmbeddedUsers>(`/users/search/byName`, {params})
      .map(response => response._embedded.users);
  }
}

class EmbeddedUsers {
  _embedded: { users: User[] };
}
