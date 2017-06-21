import {Component, Input, OnInit, ViewChild} from "@angular/core";
import {AgendaEntry} from "../shared/agenda.model";

@Component({
    templateUrl: "./agenda-entry.component.html",
    selector: 'cf-entry'
})
export class AgendaEntryComponent implements OnInit {
    @Input() entry: AgendaEntry;

    ngOnInit(): void {
    }
}
