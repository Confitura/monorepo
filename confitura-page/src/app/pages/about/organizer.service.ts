import {Injectable} from '@angular/core';
import {Person} from './person.model';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import {HttpConfiguration} from '../../shared/http-configuration.service';
import * as _ from 'lodash';
import {HttpClient} from '@angular/common/http';

@Injectable()
export class OrganizerService {
  constructor(private http: HttpClient,
              private configuration: HttpConfiguration) {
  }

  getAllOrganizers() {
    return this.getAllFor('admins');
  }

  getAllVolunteers() {
    return this.getAllFor('volunteers');
  }

  private getAllFor(type: string): Observable<Person[]> {
    return this.http.get<any>(`${this.configuration.apiServer}/users/search/${type}`)
      .map((result: any) => result['_embedded']['users'] as Person[])
      .map((persons: Person[]) => _.shuffle(persons));
  }

}
