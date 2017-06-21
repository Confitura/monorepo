import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {AgendaService} from "./agenda.service";
import {Room} from "./room.model";
import {AgendaEntry} from "./agenda.model";
import {PresentationService} from "../../profile/shared/presentation.service";
import {Presentation} from "../../profile/shared/presentation.model";
import {TimeSlot} from "./time-slot.model";
@Component({
    templateUrl: "./agenda-entry.component.html",
    selector: "cf-agenda-entry"

})
export class AgendaEntryComponent implements OnInit {

    @Input() entry: AgendaEntry;
    @Input() timeSlot: TimeSlot = null;
    @Input() room: Room = null;

    @Output() changed: EventEmitter<any> = new EventEmitter();

    presentation: Presentation;
    addingLabel = false;


    constructor(private  service: AgendaService, private presentationService: PresentationService) {
    }

    ngOnInit(): void {
        if (this.entry && this.entry.presentationId) {
            this.presentationService.getOne(this.entry.presentationId).subscribe(it => this.presentation = it)
        }
    }

    addLabel(label: string) {
        let room = null, timeSlot = null;

        if (this.room) {
            room = this.room._links.self.href
        }
        if (this.timeSlot) {
            timeSlot = this.timeSlot._links.self.href
        }
        this.service.addEntry({
            room: room,
            timeSlot: timeSlot,
            label: label
        }).subscribe(it => this.changed.emit({}))
        this.addingLabel = false;
    }

    addPresentation() {

    }


}
