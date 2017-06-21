import {Component, OnInit, ViewChild} from "@angular/core";
import {AgendaService} from "../../pages/agenda/shared/agenda.service";
import {AgendaEntry} from "../../pages/agenda/shared/agenda.model";
import {HttpConfiguration} from "../../shared/http-configuration.service";
import {CurrentUser} from "../../security/current-user.service";
import {Observable} from "rxjs/Observable";
import {TimeSlot} from "../../pages/agenda/shared/time-slot.model";
import {Room} from "../../pages/agenda/shared/room.model";
import {PresentationPickerComponent} from "./presentaion-picker/presentaion-picker.component";
@Component({
    templateUrl: "./agenda.component.html"
})
export class AgendaComponent implements OnInit {


    slots: TimeSlot[] = [];
    rooms: Room[] = [];
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
        });
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
            displayOrder: this.slots.length,
            forAllRooms: forAllRooms
        }).subscribe(it => this.refresh())
    }
}
