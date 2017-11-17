import {Injectable} from '@angular/core';
import {CustomHttp} from '../../shared/custom-http.service';
import {Response} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import {User} from './user.model';
import 'rxjs/add/operator/map';

@Injectable()
export class UserService {
  constructor(private http: CustomHttp) {
  }

  getBy(id: string, projection: string = null): Observable<User> {
    let url = `/users/${id}`;
    if (projection) {
      url += `?projection=${projection}`;
    }
    return this.http.get(url)
      .map((response: Response) => response.json() as User);
  }

  save(user: User) {
    return this.http.post(`/users`, user);
  }

  getAll(): Observable<User[]> {
    return this.http.get(`/users`)
      .map((response: Response) => response.json()['_embedded']['users'] as User[]);
  }

  getAllSpeakers(): Observable<User[]> {
    return this.http.get(`/users/search/speakers`)
      .map((response: Response) => response.json()['_embedded']['users'] as User[]);

  }

  markAsVolunteer(user: User, isVolunteer: boolean): Observable<Response> {
    return this.http.post(`/users/${user.id}/volunteer/${isVolunteer}`, {});
  }


  find(query: string): Observable<User[]> {
    return this.http.get(`/users/search/byName`, {search: {query: query}})
      .map((response: Response) => {
        return response.json()['_embedded']['users']  as User[];
      });
  }
}
