import {Component, Input} from '@angular/core';
import {AgendaEntry} from '../shared/agenda.model';

@Component({
  templateUrl: './agenda-entry.component.html',
  selector: 'cf-entry',
  styleUrls: ['./agenda-entry.component.scss']
})
export class AgendaEntryComponent {
  @Input() entry: AgendaEntry;

}
