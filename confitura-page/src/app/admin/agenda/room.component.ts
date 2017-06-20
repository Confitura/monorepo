import {Component, Input, OnInit} from "@angular/core";
import {AgendaService} from "./agenda.service";
import {AgendaEntry} from "./agenda.model";
import {HttpConfiguration} from "../../shared/http-configuration.service";
import {CurrentUser} from "../../security/current-user.service";
import {Observable} from "rxjs/Observable";
import {TimeSlot} from "./time-slot.model";
import {Room} from "./room.model";
@Component({
    templateUrl: "./room.component.html",
    selector: "cf-room"

})
export class RoomComponent implements OnInit {

    @Input() room: Room;

    ngOnInit(): void {
    }

}
