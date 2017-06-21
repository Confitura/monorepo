import {Component, ElementRef, Input, OnInit, ViewChild} from "@angular/core";
import {AgendaEntry} from "../shared/agenda.model";

@Component({
    templateUrl: "./entry-modal.component.html",
    selector: 'cf-entry-modal'
})
export class AgendaEntryModalComponent implements OnInit {
    @Input() entry: AgendaEntry;


    constructor(private el: ElementRef) {
    }

    ngOnInit(): void {
    }


    show() {
        // $(this.el.nativeElement.firstChild).modal({show: true})
    }
}
