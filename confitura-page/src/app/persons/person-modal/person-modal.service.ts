import {Injectable, EventEmitter} from "@angular/core";
import {Person} from "../../pages/about/person.model";
import {User} from "../../pages/profile/user.model";
@Injectable()
export class PersonModalService {
    changed: EventEmitter<User> = new EventEmitter<User>();

    showFor(user:User){
        this.changed.emit(user);
    }
}