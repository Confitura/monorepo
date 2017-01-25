import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Person} from "./person.model";
import {Observable} from "rxjs";
@Injectable()
export class OrganizerService{
    constructor(private http:Http){}

    getAllOrganizers(){
        return this.getAllFor("main");
    }

    getAllVolunteers(){
        return this.getAllFor("volunteers");
    }

    private getAllFor(type:string):Observable<Person[]>{
        return this.http.get(`http://c4p.confitura.pl/api/hosts/${type}`)
            .map((result: Response) => result.json() as Person[]);
    }

}