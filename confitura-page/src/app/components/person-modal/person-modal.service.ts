import {Injectable, EventEmitter} from "@angular/core";
import {Person} from "../../pages/about/person.model";
@Injectable()
export class PersonModalService {
    changed: EventEmitter<Person> = new EventEmitter<Person>();

    showFor(person:Person){
        this.changed.emit(person);
    }
}