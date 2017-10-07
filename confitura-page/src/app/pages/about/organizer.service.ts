import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Person} from "./person.model";
import {Observable} from "rxjs/Observable";
import "rxjs/add/operator/map";
import {HttpConfiguration} from "../../shared/http-configuration.service";

const _shuffle = require("lodash/shuffle");

@Injectable()
export class OrganizerService {
    constructor(private http: Http, private configuration: HttpConfiguration) {
    }

    getAllOrganizers() {
        return this.getAllFor("admins");
    }

    getAllVolunteers() {
        return this.getAllFor("volunteers");
    }

    private getAllFor(type: string): Observable<Person[]> {
        return this.http.get(`${this.configuration.apiServer}/users/search/${type}`)
            .map((result: Response) => result.json()['_embedded']['users'] as Person[])
            .map((persons: Person[]) => _shuffle(persons));
    }

}