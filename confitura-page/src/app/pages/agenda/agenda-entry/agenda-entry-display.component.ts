import {Component, EventEmitter, Input, Output} from '@angular/core';
import {AgendaEntry} from '../shared/agenda.model';
import {AgendaService} from '../shared/agenda.service';

@Component({
  templateUrl: './agenda-entry-display.component.html',
  selector: 'cf-agenda-entry-display',
  styleUrls: ['./agenda-entry-display.component.scss']
})
export class AgendaEntryDisplayComponent {
  @Input() entry: AgendaEntry;
  @Output() favoriteChanged: EventEmitter<AgendaEntry> = new EventEmitter();
  @Input() showActions = true;


  constructor(private service: AgendaService) {
  }

  addToPersonalAgenda() {
    this.service.addToPersonalAgenda(this.entry.id).subscribe(() => this.favoriteChanged.emit(this.entry));
  }
}
