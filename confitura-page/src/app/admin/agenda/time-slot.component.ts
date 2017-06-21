import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {AgendaService} from "../../pages/agenda/shared/agenda.service";
import {AgendaEntry} from "../../pages/agenda/shared/agenda.model";
import {HttpConfiguration} from "../../shared/http-configuration.service";
import {CurrentUser} from "../../security/current-user.service";
import {Observable} from "rxjs/Observable";
import {TimeSlot} from "../../pages/agenda/shared/time-slot.model";
import {Room} from "../../pages/agenda/shared/room.model";
@Component({
    templateUrl: "./time-slot.component.html",
    selector: "cf-time-slot"
})
export class TimeSlotComponent implements OnInit {

    @Input() timeSlot: TimeSlot;
    @Output() changed: EventEmitter<any> = new EventEmitter();


    constructor(private  service: AgendaService) {
    }

    ngOnInit(): void {
    }

    updateLabel() {
        this.service.updateTimeSlot(this.timeSlot).subscribe(it => this.changed.emit({}))
    }

    remove(){
        this.service.removeTimeSlot(this.timeSlot.id).subscribe(it => this.changed.emit({}))
    }
}
