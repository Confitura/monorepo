import {Component, Input} from '@angular/core';
import {AgendaEntry} from '../shared/agenda.model';

@Component({
  templateUrl: './agenda-entry-display.component.html',
  selector: 'cf-agenda-entry-display',
  styleUrls: ['./agenda-entry-display.component.scss']
})
export class AgendaEntryDisplayComponent {
  @Input() entry: AgendaEntry;

}
