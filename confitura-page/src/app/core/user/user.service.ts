import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {User} from './user.model';
import {HttpClient, HttpParams} from '@angular/common/http';
import {map} from 'rxjs/operators';

@Injectable()
export class UserService {
  constructor(private http: HttpClient) {
  }

  getBy(id: string, projection: string = null): Observable<User> {
    let params = new HttpParams();
    if (projection) {
      params = params.set('projection', projection);
    }
    return this.http.get<User>(`/users/${id}`, {params});
  }

  save(user: User) {
    return this.http.post(`/users`, user);
  }

  getAll(): Observable<User[]> {
    return this.http.get<EmbeddedUsers>(`/users`)
      .pipe(map(response => response._embedded.users));
  }

  getAllSpeakers(): Observable<User[]> {
    return this.http.get<EmbeddedUsers>(`/users/search/speakers`)
      .pipe(
        map(response => response._embedded.users)
      );

  }

  markAsVolunteer(user: User, isVolunteer: boolean): Observable<any> {
    return this.http.post<any>(`/users/${user.id}/volunteer/${isVolunteer}`, {});
  }


  find(query: string): Observable<User[]> {
    const params = new HttpParams().set('query', query);
    return this.http.get<EmbeddedUsers>(`/users/search/byName`, {params})
      .pipe(map(response => response._embedded.users));
  }
}

class EmbeddedUsers {
  _embedded: { users: User[] };
}
