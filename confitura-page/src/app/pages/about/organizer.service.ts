import {Injectable} from '@angular/core';
import {Person} from './person.model';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import * as _ from 'lodash';
import {HttpClient} from '@angular/common/http';

@Injectable()
export class OrganizerService {
  constructor(private http: HttpClient) {
  }

  getAllOrganizers() {
    return this.getAllFor('admins');
  }

  getAllVolunteers() {
    return this.getAllFor('volunteers');
  }

  private getAllFor(type: string): Observable<Person[]> {
    return this.http.get<EmbeddedUsers>(`/users/search/${type}`)
      .map(result => result._embedded.users)
      .map((persons: Person[]) => _.shuffle(persons));
  }

}

class EmbeddedUsers {
  _embedded: { users: Person[] };
}
