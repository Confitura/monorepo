import {Injectable} from "@angular/core";
import {Http, Response} from "@angular/http";
import {Person} from "./person.model";
import {Observable} from "rxjs";
import {HttpConfiguration} from "../../shared/http-configuration.service";
@Injectable()
export class OrganizerService{
    constructor(private http:Http, private configuration:HttpConfiguration){}

    getAllOrganizers(){
        return this.getAllFor("organizers");
    }

    getAllVolunteers(){
        return this.getAllFor("volunteers");
    }

    private getAllFor(type:string):Observable<Person[]>{
        return this.http.get(`${this.configuration.apiServer}/${type}`)
            .map((result: Response) => result.json()['_embedded'][type] as Person[]);
    }

}