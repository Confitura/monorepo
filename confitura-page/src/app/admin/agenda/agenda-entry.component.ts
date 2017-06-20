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


    constructor(private  service: AgendaService, private presentationService: PresentationService) {
    }

    ngOnInit(): void {
        if (this.entry && this.entry.presentationId) {
            this.presentationService.getOne(this.entry.presentationId).subscribe(it => this.presentation = it)
        }
    }

    addLabel() {
        this.service.addEntry({
            room: this.room._links.self.href,
            timeSlot: this.timeSlot._links.self.href,
            label: "test"
        }).subscribe(it => this.changed.emit({}))
    }

    addPresentation() {

    }


}
