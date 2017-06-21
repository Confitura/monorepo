import {Component, OnInit} from "@angular/core";
import {TimeSlot} from "./shared/time-slot.model";
import {Room} from "./shared/room.model";
import {AgendaService} from "./shared/agenda.service";

import "./agenda.scss"
import {months} from "moment";

@Component({
    templateUrl: "./agenda.component.html"
})
export class AgendaComponent implements OnInit {

    slots: TimeSlot[] = [];
    rooms: Room[] = [];
    selectedRooms: Room[] = [];
    activeRooms: any = null;
    agenda: any[] = [];

    constructor(private  service: AgendaService) {
    }

    ngOnInit(): void {
        this.refresh();
    }

    refresh() {
        this.service.getAgenda().subscribe(it => {
            this.rooms = it.rooms;
            this.slots = it.slots;
            this.agenda = it.agenda;

            if (this.activeRooms == null) {
                this.activeRooms = {};
                for (let room of this.rooms) {
                    this.activeRooms[room.id] = true;
                }
                this.selectedRooms = this.rooms.filter(room => this.isActive(room));
            }

        });
    }

    selectRoom(room: Room) {
        this.activeRooms[room.id] = !this.activeRooms[room.id];
        this.selectedRooms = this.rooms.filter(room => this.isActive(room));
    }

    isActive(room: Room) {
        return this.activeRooms[room.id]
    }
}
