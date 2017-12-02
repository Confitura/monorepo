import {Injectable} from '@angular/core';
import {Person} from './person.model';
import {Observable} from 'rxjs/Observable';
import {map} from 'rxjs/operators';
import {HttpClient} from '@angular/common/http';
import shuffle from 'lodash.shuffle';

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
      .pipe(
        map(result => result._embedded.users),
        map((persons: Person[]) => shuffle(persons))
      );
  }

}

class EmbeddedUsers {
  _embedded: { users: Person[] };
}
