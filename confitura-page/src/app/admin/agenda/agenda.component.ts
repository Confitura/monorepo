import {Component, OnInit} from "@angular/core";
import {AgendaService} from "./agenda.service";
import {AgendaEntry} from "./agenda.model";
import {HttpConfiguration} from "../../shared/http-configuration.service";
import {CurrentUser} from "../../security/current-user.service";
import {Observable} from "rxjs/Observable";
import {TimeSlot} from "./time-slot.model";
import {Room} from "./room.model";
@Component({
    templateUrl: "./agenda.component.html"
})
export class AgendaComponent implements OnInit {
    slots: TimeSlot[];
    rooms: Room[];

    list: AgendaEntry[];
    agenda: any;

    constructor(private  service: AgendaService,
                private config: HttpConfiguration,
                private user: CurrentUser) {


    }

    ngOnInit(): void {
        this.refresh();
    }

    private refresh() {
        Observable.zip(
            this.service.getRooms(),
            this.service.getTimeSlots(),
            this.service.getAll(),
            (rooms, slots, entries) => {
                this.rooms = rooms;
                this.slots = slots;
                this.list = entries;
                let roomIdToIndex = this.idToIndex(rooms);
                let slotIdToIndex = this.idToIndex(slots);
                let matrix = this.createEmptyMatrix(slots, slotIdToIndex, rooms);

                for (let entry of entries) {
                    let slotIndex = slotIdToIndex[entry.timeSlotId];
                    if (entry.roomId == null) {
                        if (matrix[slotIndex][0] == null) {
                            matrix[slotIndex][0] = entry
                        } else {
                            alert("conflict in agenda. Two entries in same slot");
                        }
                    } else {
                        let roomIndex = roomIdToIndex[entry.roomId];
                        matrix[slotIndex][roomIndex] = entry;
                    }
                }

                this.agenda = matrix;
                return {}
            }
        ).subscribe();
    }

    private createEmptyMatrix(slots: any, slotIdToIndex: any, rooms: any) {
        let matrix = new Array(slots.length);
        for (let slot of slots) {
            let slotIndex = slotIdToIndex[slot.id];
            if (slot.forAllRooms) {
                matrix[slotIndex] = new Array(1);
            } else {
                matrix[slotIndex] = new Array(rooms.length)
            }
        }
        return matrix;
    }

    private idToIndex(entries: any[]) {
        let roomIdToIndex = {};
        let index = 0;
        for (let entry of entries) {
            roomIdToIndex[entry.id] = index++;
        }
        return roomIdToIndex;
    }

    public addRoom() {
        this.service.addRoom({
            label: "new room",
            displayOrder: this.rooms.length
        }).subscribe(it => this.refresh())
    }

    public addTimeSlot(forAllRooms: boolean) {
        this.service.addTimeSlot({
            label: "new time slot",
            displayOrder: this.rooms.length,
            forAllRooms: forAllRooms
        }).subscribe(it => this.refresh())
    }
}
