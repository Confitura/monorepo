import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {AgendaService} from "./agenda.service";
import {Room} from "./room.model";
@Component({
    templateUrl: "./room.component.html",
    selector: "cf-room"

})
export class RoomComponent implements OnInit {

    @Input() room: Room;
    @Output() changed: EventEmitter<any> = new EventEmitter();


    constructor(private  service: AgendaService) {
    }

    ngOnInit(): void {
    }


    updateLabel() {
        this.service.updateRoom(this.room).subscribe(it => this.changed.emit({}))
    }

    remove() {
        this.service.removeRoom(this.room.id).subscribe(it => this.changed.emit({}))
    }
}
