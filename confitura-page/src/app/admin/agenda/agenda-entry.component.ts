import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {AgendaService} from "./agenda.service";
import {Room} from "./room.model";
import {AgendaEntry} from "./agenda.model";
@Component({
    templateUrl: "./agenda-entry.component.html",
    selector: "cf-agenda-entry"

})
export class AgendaEntryComponent implements OnInit {

    @Input() entry: AgendaEntry;
    @Input() timeSlotId: string = null;
    @Input() roomId: string = null;
    @Output() changed: EventEmitter<any> = new EventEmitter();


    constructor(private  service: AgendaService) {
    }

    ngOnInit(): void {
    }


}
